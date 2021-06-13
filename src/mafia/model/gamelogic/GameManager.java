package mafia.model.gamelogic;

import mafia.chatroom.server.Server;
import mafia.model.GameData;
import mafia.model.element.*;
import static mafia.model.element.Phase.*;
import static mafia.model.element.Role.*;
import static mafia.model.GodMessages.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class GameManager
{
    // game server
    private final Server server;
    // game state
    private final GameData gameData;
    private Phase gamePhase;
    // game details
    private NightResult nightResult;

    public GameManager()
    {
        server = new Server(5757);
        gameData = GameData.getInstance();
        gamePhase = NOT_STARTED;
    }

    public boolean launch()
    {
        // wait for clients to join
        server.execute();
        sendMsgFromGod("No more player can join!");
        ArrayList<String> usernames = server.prepareGame();
        if (usernames.size() < 4)
        {
            sendMsgFromGod("Cannot start the game with only " + usernames.size() + " players!");
            return false;
        }
        // set up the game (create players in the gameData and set random roles to it)
        GameSetup gameSetup = new GameSetup();
        gameSetup.initialize(server.prepareGame()); // sets the game's phase to NOT_STARTED
        return true;
    }

    public void wakeup(HashSet<Player> players)
    {
        if (players != null)
        {
            for (Player p : players)
                p.wakeup();
        }
    }

    public void sleep(HashSet<Player> players)
    {
        if (players != null)
        {
            for (Player p : players)
                p.goToSleep();
        }
    }

    public void wakeup(Role role)
    {
        HashSet<Player> players = gameData.getAlivePlayers();
        for (Player p : players)
        {
            if (p.getRole() == role)
                p.wakeup();
        }
    }

    public void sleep(Role role)
    {
        HashSet<Player> players = gameData.getAlivePlayers();
        for (Player p : players)
        {
            if (p.getRole() == role)
                p.goToSleep();
        }
    }

    public void sendMsgFromGod(String text) {
        server.broadcast(new Message(text, "GOD"));
    }

    public void nextPhase()
    {
        switch (gamePhase)
        {
            case NOT_STARTED ->                       gamePhase = INTRODUCTION_NIGHT;
            case INTRODUCTION_NIGHT, NIGHT_ARNOLD ->  gamePhase = DAY;
            case DAY ->                               gamePhase = ELECTION_DAY;
            case ELECTION_DAY ->                      gamePhase = DAY_MAYOR;
            case DAY_MAYOR ->                         gamePhase = NIGHT_MAFIA;
            case NIGHT_MAFIA ->                       gamePhase = NIGHT_LECTOR;
            case NIGHT_LECTOR ->                      gamePhase = NIGHT_DOCTOR;
            case NIGHT_DOCTOR ->                      gamePhase = NIGHT_DETECTIVE;
            case NIGHT_DETECTIVE ->                   gamePhase = NIGHT_SNIPER;
            case NIGHT_SNIPER ->                      gamePhase = NIGHT_THERAPIST;
            case NIGHT_THERAPIST ->                   gamePhase = NIGHT_ARNOLD;
        }
        // notify the players about the game's phase
        if (gamePhase == DAY || gamePhase == ELECTION_DAY || gamePhase == INTRODUCTION_NIGHT)
            sendMsgFromGod("It's " + gamePhase);
        else if (gamePhase == NIGHT_MAFIA)
            sendMsgFromGod("The day has ended, time to go to bed...");
        // update the game's phase in the gameData
        gameData.setCurrentPhase(gamePhase);
    }

    public void introduce()
    {
        if (gamePhase == INTRODUCTION_NIGHT)
        {
            sleep(gameData.getAlivePlayers());

            // introduce mafias
            wakeup(gameData.getMafias());
            sendMsgFromGod(welcomeMafias());
            sleep(gameData.getMafias());

            // introduce godfather
            wakeup(GODFATHER);
            sendMsgFromGod(welcomeGodfather());
            sleep(GODFATHER);

            // introduce doctor lector
            if (gameData.hasLector)
            {
                wakeup(LECTOR);
                sendMsgFromGod(welcomeLector());
                sleep(LECTOR);
            }

            // introduce the normal citizens
            wakeup(CITIZEN);
            sendMsgFromGod(welcomeCitizens());
            sleep(CITIZEN);

            // introduce the city's doctor
            wakeup(DOCTOR);
            sendMsgFromGod(welcomeDoctor());
            sleep(DOCTOR);

            // introduce the doctor to the mayor
            if (gameData.hasMayor)
            {
                wakeup(MAYOR);
                sendMsgFromGod(welcomeMayor(gameData.findPlayer(DOCTOR)));
                sleep(MAYOR);
            }

            // detective
            wakeup(DETECTIVE);
            sendMsgFromGod(welcomeDetective());
            sleep(DETECTIVE);

            // sniper
            if (gameData.hasSniper)
            {
                wakeup(SNIPER);
                sendMsgFromGod(welcomeSniper());
                sleep(SNIPER);
            }

            // therapist
            if (gameData.hasTherapist)
            {
                wakeup(THERAPIST);
                sendMsgFromGod(welcomeTherapist());
                sleep(THERAPIST);
            }

            // arnold
            if (gameData.hasArnold)
            {
                wakeup(ARNOLD);
                sendMsgFromGod(welcomeArnold());
                sleep(ARNOLD);
            }

            wakeup(gameData.getAlivePlayers());
        }
    }

    public void collectVotes()
    {
        gameData.setElectionIsOn(true);
        gameData.setLastElection(new Election());

        waiting(20000);
        sendMsgFromGod("10 seconds left!");
        waiting(10000);
        sendMsgFromGod("Time is up!");
        gameData.setElectionIsOn(false);
    }

    public void godfatherDecision()
    {
        Message godfatherMsg;
        Player candidate = null, sender;
        gameData.getLastElection().setMafiaElection(true);

        if (gamePhase == NIGHT_MAFIA)
        {
            if (gameData.getMafias().size() == 1 || !gameData.hasGodfather) {
                candidate = gameData.getLastElection().calFinalResult();
            }
            else
            {
                // asking godfather to make the final decision
                sleep(MAFIA);
                wakeup(GODFATHER);
                sendMsgFromGod(gameData.getLastElection().toString());
                sendMsgFromGod("What's the final decision:");

                waiting(20000);
                godfatherMsg = gameData.getLastMessage();
                if (godfatherMsg != null)
                {
                    sender = gameData.findPlayer(godfatherMsg.getSender());
                    // if godfather did not answer, god will calculate the final result
                    if (sender != null && sender.getRole() != GODFATHER)
                        candidate = gameData.getLastElection().calFinalResult();
                    else
                    {
                        candidate = gameData.findPlayer(godfatherMsg.getText());
                        // if godfather's answer is invalid, god will calculate the final result
                        if (candidate == null)
                            candidate = gameData.getLastElection().calFinalResult();
                        // godfather has answered
                        else {
                            gameData.getLastElection().setFinalCandidate(candidate);
                            sendMsgFromGod("OK");
                        }
                    }
                }
                sleep(GODFATHER);
            }
            kill(candidate, MAFIA);
        }
    }

    public void kill(Player target, Role murderer)
    {
        if (target != null)
        {
            // handling roles that can heal themselves for one time in the game
            if (target.getRole() == DOCTOR && !gameData.doctorHasHealedHimself)
                gameData.doctorHasHealedHimself = true;
            else if (target.getRole() == LECTOR && !gameData.lectorHasHealedHimself)
                gameData.lectorHasHealedHimself = true;
            else if (target.getRole() == ARNOLD && !gameData.mafiaHasShootArnold && murderer == MAFIA)
                gameData.mafiaHasShootArnold = true;
            else if (!nightResult.isProtectedByLector(target))
                nightResult.addMurder(target, murderer);
        }
    }

    public void shoot()
    {
        if (gamePhase == NIGHT_SNIPER && gameData.hasSniper)
        {
            Player target, sender;
            wakeup(SNIPER);
            sendMsgFromGod("Do you want to shoot anyone? (yes/no)");
            waiting(20000);
            Message answer = gameData.getLastMessage();
            // if the sniper does not reply in this 30 seconds, god will move on to the next night action
            if (answer != null)
            {
                sender = gameData.findPlayer(answer.getSender());
                if (sender != null && sender.getRole() == SNIPER
                        && answer.getText().equalsIgnoreCase("yes"))
                {
                    sendMsgFromGod("Who do you want to kill?");
                    waiting(10000);
                    answer = gameData.getLastMessage();
                    target = gameData.findPlayer(answer.getText());

                    if (target != null)
                    {
                        if (target.getRole() != MAFIA && target.getRole() != GODFATHER && target.getRole() != LECTOR)
                        {
                            sendMsgFromGod("Oops! You killed one of your team mates buddy...\n" +
                                    "You get removed from the game...");
                            nightResult.addRemovedPlayer(gameData.findPlayer(SNIPER));
                        }
                        else
                        {
                            sendMsgFromGod("OK");
                            kill(target, SNIPER);
                        }
                    }
                }
            }
            sleep(SNIPER);
        }
    }

    public void heal()
    {
        if (gamePhase == NIGHT_DOCTOR && gameData.hasDoctor)
        {
            wakeup(DOCTOR);
            sendMsgFromGod("Who do you want to heal tonight?");
            waiting(20000);
            Message answer = gameData.getLastMessage();
            Player target, sender;
            if (answer != null)
            {
                sender = gameData.findPlayer(answer.getSender());
                if (sender.getRole() == DOCTOR)
                {
                    target = gameData.findPlayer(answer.getText());
                    if (target != null && nightResult.getMurders().containsKey(target))
                    {
                        sendMsgFromGod("You saved " + target.getUsername() + " !");
                        nightResult.addHeal(target, DOCTOR);
                    }
                }
            }
            sleep(DOCTOR);
        }
    }

    public void protect()
    {
        if (gamePhase == NIGHT_LECTOR && gameData.hasLector && gameData.hasSniper)
        {
            wakeup(LECTOR);
            sendMsgFromGod("Which one of the mafias do you want to protect from sniper?");
            waiting(20000);
            Message answer = gameData.getLastMessage();
            Player target, sender;
            if (answer != null)
            {
                sender = gameData.findPlayer(answer.getSender());
                if (sender != null && sender.getRole() == LECTOR)
                {
                    target = gameData.findPlayer(answer.getText());
                    // check if the target exists in the game and is on mafia team
                    if (target != null && (target.getRole() == MAFIA || target.getRole() == GODFATHER)) {
                        nightResult.addHeal(target, LECTOR);
                        sendMsgFromGod("OK");
                    }
                }
            }
            sleep(LECTOR);
        }
    }

    public void detectPlayers()
    {
        if (gamePhase == NIGHT_DETECTIVE && gameData.hasDetective)
        {
            Message answer;
            Player sender;

            wakeup(DETECTIVE);
            sendMsgFromGod("Whose role do you want to find out?");
            waiting(20000);
            answer = gameData.getLastMessage();

            if (answer != null)
            {
                sender = gameData.findPlayer(answer.getSender());
                if (sender != null && sender.getRole() == DETECTIVE)
                {
                    Player target = gameData.findPlayer(answer.getText());
                    if (target != null)
                    {
                        if (target.getRole() == GODFATHER || target.getRole() == CITIZEN)
                            sendMsgFromGod("Cannot tell you " + target.getUsername() + " role!");
                        else
                            sendMsgFromGod(target.getUsername() + " is " + target.getRole());
                    }
                }
            }
            sleep(DETECTIVE);
        }
    }

    public void silent()
    {
        if (gamePhase == NIGHT_THERAPIST && gameData.hasTherapist)
        {
            wakeup(THERAPIST);
            sendMsgFromGod("Do you want to silent anyone? (yes/no)");
            waiting(20000);
            Message answer = gameData.getLastMessage();
            Player sender, target;
            if (answer != null)
            {
                sender = gameData.findPlayer(answer.getSender());
                if (sender != null && sender.getRole() == THERAPIST
                        && answer.getText().equalsIgnoreCase("yes"))
                {
                    sendMsgFromGod("Who do you want to silent?");
                    waiting(10000);
                    answer = gameData.getLastMessage();
                    target = gameData.findPlayer(answer.getText());
                    if (target != null)
                    {
                        sendMsgFromGod(target.getUsername() + " is silenced for the next day");
                        target.setCanSpeak(false);
                        nightResult.setSilencedPlayer(target);
                        sleep(THERAPIST);
                    }
                }
            }
            sleep(THERAPIST);
        }
    }

    public void inquiry()
    {
        if (gamePhase == NIGHT_ARNOLD
                && !gameData.getDeadPlayers().isEmpty() && gameData.arnoldInquiries < 2
                && gameData.hasArnold)
        {
            wakeup(ARNOLD);
            sendMsgFromGod("Do you want to know the removed roles? (yes/no)");
            waiting(20000);
            Message answer = gameData.getLastMessage();
            Player sender;
            if (answer != null)
            {
                sender = gameData.findPlayer(answer.getSender());
                if (sender != null && sender.getRole() == ARNOLD
                        && answer.getText().equalsIgnoreCase("yes"))
                {
                    sendMsgFromGod("I will tell you the list of removed roles in the morning");
                    nightResult.setArnoldHadInquiry(true);
                    gameData.arnoldInquiries++;
                }
            }
            sleep(ARNOLD);
        }
    }

    /**
     * Removes all the dead/removed players from "gameData.alivePlayers"
     * Adds them to "gameData.deadPlayers"
     */
    public void analyzeNightResult()
    {
        for (Map.Entry<Player, Role> entry : nightResult.getMurders().entrySet())
        {
            entry.getKey().wakeup();
            sendMsgFromGod(entry.getValue() + " killed you!");
            goodbye(entry.getKey());
        }
        for (Player p : nightResult.getRemovedPlayers())
            goodbye(p);
    }

    public boolean cancelElection()
    {
        boolean cancel = false;
        if (gamePhase == DAY_MAYOR && gameData.hasMayor)
        {
            wakeup(MAYOR);
            sendMsgFromGod("Are you ok with the election? (yes/no)");
            waiting(20000); // wait for the mayor to answer
            Message answer = gameData.getLastMessage();
            Player sender;
            if (answer != null)
            {
                sender = gameData.findPlayer(answer.getSender());
                if (sender != null && sender.getRole() == MAYOR)
                    cancel = answer.getText().equalsIgnoreCase("no");
                sleep(MAYOR);
            }
        }
        return cancel;
    }

    public void goodbye(Player removedPlayer)
    {
        removedPlayer.wakeup();
        sendMsgFromGod("You got removed from the game\n" +
                "Enter anything if you want to stay as a viewer (You can't talk anymore though)...");
        waiting(20000);
        gameData.handlePlayerRemoval(removedPlayer);
        if (!gameData.getLastMessage().getSender().equals(removedPlayer.getUsername()))
        {
            // removes the player's corresponding client handler from the server
            server.shutDownClient(removedPlayer.getUsername());
        }
        else
        {
            removedPlayer.setCanSpeak(false);  // stays as a viewer, all the messages are sent to him but he can't speak
            gameData.getAlivePlayers().remove(removedPlayer);
            gameData.getDeadPlayers().add(removedPlayer);
        }
    }

    public void announceNightResult()
    {
        wakeup(gameData.getAlivePlayers());
        sendMsgFromGod(nightResult.toString());
        if (nightResult.arnoldHadInquiry())
        {
            StringBuilder inquiry = new StringBuilder("The removed roles are:\n");
            for (Player p : gameData.getDeadPlayers())
                inquiry.append(p.getRole()).append(" ");
            sendMsgFromGod(inquiry.toString());
        }
        sleep(gameData.getAlivePlayers());
    }

    public void chatMode()
    {
        wakeup(gameData.getAlivePlayers());
        sendMsgFromGod("""
                CHATROOM mode
                Discuss whoever your think might have to get removed from the game...
                Note that you only have 90 seconds until the election time!""");

        waiting(80000); // chatroom mode should be 5 minutes
        sendMsgFromGod("You've got 10 seconds left!");
        waiting(10000);
        sendMsgFromGod("Chatroom mode has ended!");
        sleep(gameData.getAlivePlayers());
    }

    public void election()
    {
        wakeup(gameData.getAlivePlayers());
        sendMsgFromGod("Who do you think should be hanged today?");
        collectVotes();
        sendMsgFromGod(gameData.getLastElection().toString()); // notify everyone about who chose whom to get killed
        waiting(5000);
        Player candidate = gameData.getLastElection().calFinalResult();
        sleep(gameData.getAlivePlayers());
        nextPhase(); // changes to day mayor
        if (candidate != null)
        {
            if (cancelElection()) // the mayor has canceled the election
            {
                wakeup(gameData.getAlivePlayers());
                sendMsgFromGod("The election is canceled!");
            }
            else
            {
                goodbye(candidate);
                wakeup(gameData.getAlivePlayers());
                sendMsgFromGod(candidate.getUsername() + " got removed from the game!");
            }
        }
        sleep(gameData.getAlivePlayers());
    }

    public void announceAlivePlayers()
    {
        wakeup(gameData.getAlivePlayers());
        sendMsgFromGod("Alive players: " + gameData.getListOfAlivePlayers());
        sleep(gameData.getAlivePlayers());
    }

    public void prepareNight()
    {
        nightResult = gameData.getLastNightResult();
        if (nightResult != null)
        {
            Player lastSilencedPlayer = nightResult.getSilencedPlayer();
            if (lastSilencedPlayer != null)
                lastSilencedPlayer.setCanSpeak(true);
        }
        nightResult = new NightResult();
    }

    public void mafiaNight()
    {
        StringBuilder str = new StringBuilder();
        for (Player p : gameData.getMafias())
            str.append(p.getUsername()).append(" ");
        str.append(", who do you want to kill tonight?😈");
        if (gamePhase == NIGHT_MAFIA)
        {
            wakeup(gameData.getMafias());
            sendMsgFromGod(str.toString()); // notify the mafias that they have to choose someone to kill right now
            collectVotes();
            godfatherDecision();
            sleep(gameData.getMafias());
        }
    }

    public void endNight()
    {
        analyzeNightResult();
        announceNightResult();
        gameData.setLastNightResult(nightResult);
    }

    public void waiting(long ms)
    {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void endGame()
    {
        // announce the winner to all the players and end the whole game
        Winner winner = gameData.getWinner();
        if (winner != Winner.UNKNOWN)
        {
            wakeup(gameData.getAlivePlayers());
            sendMsgFromGod("The game has ended\nThe winner is " + winner);
        }
        server.shutDownServer();
    }





















    public void doDayActs()
    {
        wakeup(gameData.getAlivePlayers());
        sendMsgFromGod("Alive players: " + gameData.getListOfAlivePlayers());
        sendMsgFromGod("""
                CHATROOM mode
                Discuss whoever your think might have to get removed from the game...
                Note that you only have 90 seconds until the election time!""");

        waiting(80000);
        sendMsgFromGod("You've got 10 seconds left!");
        waiting(10000);

        nextPhase();
        collectVotes();
        Player candidate = gameData.getLastElection().calFinalResult();
        sleep(gameData.getAlivePlayers());
        if (gameData.hasMayor)
        {
            if (cancelElection()) // the mayor canceled the election
            {
                wakeup(gameData.getAlivePlayers());
                sendMsgFromGod("The election is canceled!");
                sleep(gameData.getAlivePlayers());
                return;
            }
        }
        goodbye(candidate);
        wakeup(gameData.getAlivePlayers());
        sendMsgFromGod(candidate.getUsername() + " got removed from the game!");
        sleep(gameData.getAlivePlayers());
    }

    public void doNightActs()
    {
        wakeup(gameData.getAlivePlayers());
        sendMsgFromGod("Alive players: " + gameData.getListOfAlivePlayers());
        sleep(gameData.getAlivePlayers());
        // first, we need to set the prev silenced player's "canSpeak" to true
        nightResult = gameData.getLastNightResult();
        if (nightResult != null)
        {
            Player lastSilencedPlayer = nightResult.getSilencedPlayer();
            if (lastSilencedPlayer != null)
                lastSilencedPlayer.setCanSpeak(true);
        }
        nightResult = new NightResult();
        if (gamePhase == NIGHT_MAFIA)
        {
            wakeup(MAFIA);
            collectVotes();
            godfatherDecision();
            sleep(gameData.getMafias());
        }
        else return; // the method should continue doing its tasks only if the current phase is night mafia
        nextPhase();

        if (gameData.hasLector) protect();
        nextPhase();

        if (gameData.hasDoctor) heal();
        nextPhase();

        if (gameData.hasDetective) detectPlayers();
        nextPhase();

        if (gameData.hasSniper) shoot();
        nextPhase();

        if (gameData.hasTherapist) silent();
        nextPhase();

        if (gameData.hasArnold) inquiry();

        analyzeNightResult();
        announceNightResult();
        gameData.setLastNightResult(nightResult);
    }
}
