package mafia.model.gamelogic;

import mafia.chatroom.server.Server;
import mafia.model.GameData;
import mafia.model.element.*;
import static mafia.model.element.Phase.*;
import static mafia.model.element.Role.*;
import static mafia.model.GodMessages.*;

import java.util.ArrayList;
import java.util.HashSet;

public class GameManager
{
    // game server
    private final Server server;
    // game state
    private final GameData gameData;
    private Phase gamePhase; // TODO remove this field, change all gamePhase uses (use "current game phase" in gameData)
    // game details
    private NightResult nightResult;

    public GameManager()
    {
        server = new Server(5757);
        gameData = GameData.getInstance();
        gamePhase = NOT_STARTED;
    }

    public boolean setUpTheServer()
    {
        // wait for clients to join
        server.execute();
        sendMsgFromGod("Time is up everyone!");
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
            case INTRODUCTION_NIGHT, DAY_MAYOR ->     gamePhase = NIGHT_MAFIA;
            case DAY ->                               gamePhase = ELECTION_DAY;
            case ELECTION_DAY ->                      gamePhase = DAY_MAYOR;
            case NIGHT_MAFIA ->                       gamePhase = NIGHT_LECTOR;
            case NIGHT_LECTOR ->                      gamePhase = NIGHT_DOCTOR;
            case NIGHT_DOCTOR ->                      gamePhase = NIGHT_DETECTIVE;
            case NIGHT_DETECTIVE ->                   gamePhase = NIGHT_SNIPER;
            case NIGHT_SNIPER ->                      gamePhase = NIGHT_THERAPIST;
            case NIGHT_THERAPIST ->                   gamePhase = NIGHT_ARNOLD;
            case NIGHT_ARNOLD ->                      gamePhase = DAY;
        }
        // TODO add a message for each game phase in GodMessages and send a better message to the players
        if (gamePhase == INTRODUCTION_NIGHT || gamePhase == DAY || gamePhase == ELECTION_DAY )
        {
            // notify the players about the game's phase
            sendMsgFromGod("GAME PHASE : " + gamePhase + "\n");
        }
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

    public void startElection()
    {
        if (gamePhase == NIGHT_MAFIA)
            sendMsgFromGod("Who do you want to kill tonight?");
        else if (gamePhase == ELECTION_DAY)
            sendMsgFromGod("Choose someone for the election");

        gameData.setElectionIsOn(true);
        gameData.setLastElection(new Election());

        waiting(20000);
        sendMsgFromGod("10 seconds left");
        waiting(10000);
        gameData.setElectionIsOn(false);
    }

    // TODO return value unnecessary
    public void godfatherDecision()
    {
        Message godfatherMsg;
        Player godfatherCandidate;
        gameData.getLastElection().setMafiaElection(true);

        if (gamePhase == NIGHT_MAFIA)
        {
            if (gameData.getMafias().size() == 1 || !gameData.hasGodfather)
                godfatherCandidate = gameData.getLastElection().calFinalResult();
            else
            {
                // asking godfather to make the final decision
                sleep(MAFIA);
                wakeup(GODFATHER);
                sendMsgFromGod(gameData.getLastElection().toString());
                sendMsgFromGod("What's the final decision:\n");

                waiting(10000);
                godfatherMsg = gameData.getLastMessage();
                // if godfather did not answer, god will calculate the final result
                if (gameData.findPlayer(godfatherMsg.getSender()).getRole() != GODFATHER) {
                    godfatherCandidate = gameData.getLastElection().calFinalResult();
                }
                // godfather has answered
                else
                {
                    godfatherCandidate = gameData.findPlayer(godfatherMsg.getText());
                    // if godfather's answer is invalid, god will calculate the final result
                    if (godfatherCandidate == null)
                        godfatherCandidate = gameData.getLastElection().calFinalResult();
                    else gameData.getLastElection().setFinalCandidate(godfatherCandidate);
                }
                sleep(GODFATHER);
            }
            kill(godfatherCandidate, MAFIA);
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
        if (gamePhase == NIGHT_SNIPER)
        {
            wakeup(SNIPER);
            sendMsgFromGod("Do you want to shoot anyone? (yes/no)\n");
            waiting(10000);
            Message answer = gameData.getLastMessage();
            // if the sniper does not reply in this 5 seconds, god will move on to the next night action
            if (gameData.findPlayer(answer.getSender()).getRole() == SNIPER)
            {
                if (answer.getText().equalsIgnoreCase("yes"))
                {
                    sendMsgFromGod("Who do you want to kill?\n");
                    waiting(10000);
                    answer = gameData.getLastMessage();
                    Player target = gameData.findPlayer(answer.getText());

                    if (target != null)
                    {
                        if (target.getRole() != MAFIA && target.getRole() != GODFATHER && target.getRole() != LECTOR)
                        {
                            sendMsgFromGod("""
                                Oops! You killed one of your team mates buddy...
                                You get removed from the game...
                                """);
                            nightResult.addRemovedPlayer(gameData.findPlayer(SNIPER));
                        }
                        else kill(target, SNIPER);
                    }
                }
            }
            sleep(SNIPER);
        }
    }

    public void heal()
    {
        if (gamePhase == NIGHT_DOCTOR)
        {
            wakeup(DOCTOR);
            sendMsgFromGod("Who do you want to heal tonight?\n");
            waiting(10000);
            Message answer = gameData.getLastMessage();
            Player target;
            if (gameData.findPlayer(answer.getSender()).getRole() == DOCTOR)
            {
                target = gameData.findPlayer(answer.getText());
                if (target != null && nightResult.getMurders().containsKey(target))
                {
                    sendMsgFromGod("You saved " + target.getUsername() + " !\n");
                    nightResult.addHeal(target, DOCTOR);
                }
            }
            sleep(DOCTOR);
        }
    }

    public void protect()
    {
        if (gamePhase == NIGHT_LECTOR)
        {
            wakeup(LECTOR);
            sendMsgFromGod("Which one of the mafias do you want to protect from the sniper?\n");
            waiting(10000);
            Message answer = gameData.getLastMessage();
            Player target;
            if (gameData.findPlayer(answer.getSender()).getRole() == LECTOR)
            {
                target = gameData.findPlayer(answer.getText());
                // check if the target exists in the game and is on mafia team
                if (target != null && (target.getRole() == MAFIA || target.getRole() == GODFATHER))
                    nightResult.addHeal(target, LECTOR);
            }
            sleep(LECTOR);
        }
    }

    public void detectPlayers()
    {
        if (gamePhase == NIGHT_DETECTIVE)
        {
            Message lastMessage;
            Player sender;

            wakeup(DETECTIVE);
            sendMsgFromGod("Whose role do you want to find out?");
            waiting(3000);
            lastMessage = gameData.getLastMessage();
            sender = gameData.findPlayer(lastMessage.getSender());

            if (sender.getRole() == DETECTIVE)
            {
                Player target = gameData.findPlayer(lastMessage.getText());
                if (target != null)
                {
                    if (target.getRole() == GODFATHER || target.getRole() == CITIZEN)
                        sendMsgFromGod("Cannot tell you " + target.getUsername() + " role!");
                    else
                        sendMsgFromGod(target.getUsername() + " is " + target.getRole());
                }
            }
            sleep(DETECTIVE);
        }
    }

    public void silent()
    {
        if (gamePhase == NIGHT_THERAPIST)
        {
            wakeup(THERAPIST);
            sendMsgFromGod("Do you want to silent anyone? (yes/no)\n");
            waiting(10000);
            Message answer = gameData.getLastMessage();
            if (gameData.findPlayer(answer.getSender()).getRole() == THERAPIST)
            {
                sendMsgFromGod("Who do you want to silent?\n");
                waiting(10000);
                answer = gameData.getLastMessage();
                Player target = gameData.findPlayer(answer.getText());
                if (target != null)
                {
                    sendMsgFromGod(target.getUsername() + " is silenced for the next day\n");
                    target.setCanSpeak(false); // TODO tell the target that they cannot speak for the next day
                }
            }
            sleep(THERAPIST);
        }
    }

    public void inquiry()
    {
        if (gamePhase == NIGHT_ARNOLD)
        {
            if (gameData.arnoldInquiries == 2)
                return;
            wakeup(ARNOLD);
            sendMsgFromGod("Do you want to know the removed roles? (yes/no)\n");
            waiting(10000);
            Message answer = gameData.getLastMessage();
            if (gameData.findPlayer(answer.getSender()).getRole() == ARNOLD
                    && answer.getText().equalsIgnoreCase("yes"))
            {
                sendMsgFromGod("I will tell you the list of removed roles in the morning\n");
                nightResult.setArnoldHadInquiry(true);
                gameData.arnoldInquiries++;
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

    }

    public boolean cancelElection()
    {
        boolean cancel = false;
        if (gamePhase == DAY_MAYOR)
        {
            wakeup(MAYOR);
            sendMsgFromGod("Are you ok with the election? (yes/no)");
            waiting(10000); // wait for the mayor to answer
            Message answer = gameData.getLastMessage();
            if (gameData.findPlayer(answer.getSender()).getRole() == MAYOR)
            {
                cancel = answer.getText().equalsIgnoreCase("yes")
                        || answer.getText().equalsIgnoreCase("ok");
            }
            sleep(MAYOR);
        }
        return cancel;
    }

    // TODO complete this
    public void goodbye(Player removedPlayer)
    {
        gameData.getAlivePlayers().remove(removedPlayer);
        gameData.getDeadPlayers().add(removedPlayer);
        // TODO notify the player that they got removed from the game and ask them if they want to stay as a viewer or leave
    }

    public void doNightActs()
    {
        if (gamePhase == NIGHT_MAFIA)
        {
            // first, we need to set the prev silenced player's "canSpeak" to true
            if (gameData.getLastSilencedPlayer() != null)
                gameData.getLastSilencedPlayer().setCanSpeak(true);

            wakeup(MAFIA);
            startElection();
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
    }

    public void announceNightResult()
    {
        wakeup(gameData.getAlivePlayers());
        sendMsgFromGod(nightResult.toString());
        sleep(gameData.getAlivePlayers());
    }

    public void doDayActs()
    {
        waiting(3000); // just for the flow of the game
        wakeup(gameData.getAlivePlayers());
        sendMsgFromGod("""
                CHATROOM mode
                Discuss whoever you think might have to get removed from the game...
                Note that you only have 90 seconds until the election time!
                """);
        waiting(80000);
        sendMsgFromGod("You've got 10 seconds left!\n");
        waiting(10000);

        startElection();
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

    public void waiting(long ms)
    {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
