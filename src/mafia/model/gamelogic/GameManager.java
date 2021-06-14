package mafia.model.gamelogic;

import mafia.model.chatroom.server.Server;
import mafia.model.GameData;
import mafia.model.GodMessages;
import mafia.model.element.*;
import static mafia.model.gamelogic.PlayerManager.*;
import static mafia.model.element.Phase.*;
import static mafia.model.element.Role.*;
import static mafia.model.GodMessages.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * The whole logic of the game is handled in this class which represents God in mafia
 * @author KIMIA
 * @version 1.0
 */
public class GameManager
{
    // game server
    private final Server server;
    // game state
    private final GameData gameData;
    private Phase gamePhase;
    // game details
    private NightResult nightResult;

    /**
     * Creates a game with the port 5757
     */
    public GameManager()
    {
        server = new Server(5757);
        gameData = GameData.getInstance();
        gamePhase = NOT_STARTED;
    }

    /**
     * launches a game by executing the server, sets up the game is possible
     * @return true if the game can be started
     */
    public boolean launch()
    {
        server.execute();
        sendMsgFromGod("No more player can join!");
        ArrayList<String> usernames = server.prepareGame();
        if (usernames.size() < 4) {
            sendMsgFromGod("Cannot start the game with only " + usernames.size() + " players!");
            return false;
        }
        GameSetup gameSetup = new GameSetup();
        gameSetup.initialize(server.prepareGame());
        return true;
    }

    /**
     * Sends a text to players
     * @param text a text
     */
    public void sendMsgFromGod(String text) {
        server.broadcast(new Message(text, "GOD"));
    }

    /**
     * Switches to the next game phase
     */
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
        if (gamePhase == DAY || gamePhase == ELECTION_DAY || gamePhase == INTRODUCTION_NIGHT)
            sendMsgFromGod("It's " + gamePhase); // notify the players about the game's phase
        else if (gamePhase == NIGHT_MAFIA)
            sendMsgFromGod("The day has ended, time to go to bed...");
        gameData.setCurrentPhase(gamePhase); // update the game's phase in the gameData
    }

    /**
     * Welcomes each player to the game
     */
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

    /**
     * Sets game data's electionIsOn to true, waits for 30 seconds, and collects votes from players
     */
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

    /**
     * Asks god father about his final decision
     */
    public void godfatherDecision()
    {
        Message godfatherMsg;
        Player candidate = null, sender;
        gameData.getLastElection().setMafiaElection(true);
        if (gamePhase == NIGHT_MAFIA)
        {
            if (gameData.getMafias().size() == 1 || !gameData.hasGodfather)
                candidate = gameData.getLastElection().calFinalResult();
            else
            {
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

    /**
     * This method is used when a murderer kills a player at night. Adds the target to the night result's murders
     * @param target target to kill
     * @param murderer killer
     */
    public void kill(Player target, Role murderer)
    {
        if (target != null)
        {
            if (target.getRole() == ARNOLD && !gameData.mafiaHasShootArnold && murderer == MAFIA)
                gameData.mafiaHasShootArnold = true;
            else if (!nightResult.isProtectedByLector(target))
                nightResult.addMurder(target, murderer);
        }
    }

    /**
     * Sniper shoots someone if he wants, he either succeeds or fails and gets removed from the game
     */
    public void shoot()
    {
        if (gamePhase == NIGHT_SNIPER && gameData.hasSniper)
        {
            Player target, sender;
            wakeup(SNIPER);
            sendMsgFromGod("Do you want to shoot anyone? (yes/no)");
            waiting(20000);
            Message answer = gameData.getLastMessage();
            // if the sniper does not reply in this 20 seconds, god will move on to the next night action
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

    /**
     * Doctor heals someone if he wants
     */
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
                    if (target != null)
                    {
                        if (target.getRole() == DOCTOR && gameData.doctorHasHealedHimself) {
                            sendMsgFromGod("You can't save yourself anymore :(");
                        } else if (target.getRole() == DOCTOR) {
                            sendMsgFromGod("You saved yourself but you can't do that anymore!");
                            gameData.doctorHasHealedHimself = true;
                            nightResult.addHeal(target, DOCTOR);
                        } else {
                            sendMsgFromGod("OK");
                            nightResult.addHeal(target, DOCTOR);
                        }
                    }
                }
            }
            sleep(DOCTOR);
        }
    }

    /**
     * Lector chooses a mafia member to protect from sniper
     */
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
                    if (target != null)
                    {
                        if (target.getRole() == MAFIA || target.getRole() == GODFATHER) {
                            nightResult.addHeal(target, LECTOR);
                            sendMsgFromGod("OK");
                        } else if (target.getRole() == LECTOR) {
                            if (gameData.lectorHasHealedHimself)
                                sendMsgFromGod("You can't save yourself anymore :(");
                            else {
                                sendMsgFromGod("You saved yourself but you can't do that anymore!");
                                gameData.lectorHasHealedHimself = true;
                                nightResult.addHeal(target, LECTOR);
                            }
                        }
                    }
                }
            }
            sleep(LECTOR);
        }
    }

    /**
     * Detective chooses someone to find out about their role
     */
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

    /**
     * Therapist silences someone if he wants
     */
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

    /**
     * Arnold asks for the removed roles if he wants
     */
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
                    sendMsgFromGod("I will announce the removed roles in the morning");
                    nightResult.setArnoldHadInquiry(true);
                    gameData.arnoldInquiries++;
                }
            }
            sleep(ARNOLD);
        }
    }

    /**
     * Removes all the dead players from alivePlayers after saying goodbye to them. Adds them to deadPlayers
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

    /**
     * @return true if mayor wants to cancel the election
     */
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

    /**
     * Tells the removed player that they are dead and asks them if they want to stay as a viewer or not
     * @param removedPlayer removed player
     */
    public void goodbye(Player removedPlayer)
    {
        removedPlayer.wakeup();
        sendMsgFromGod("You got removed from the game\n" +
                "Enter anything if you want to stay as a viewer (You can't talk anymore though)...");
        waiting(20000);
        handlePlayerRemoval(removedPlayer);
        if (!gameData.getLastMessage().getSender().equals(removedPlayer.getUsername()))
            server.shutDownClient(removedPlayer.getUsername()); // removes the player's matching client handler from the server
        else
        {
            removedPlayer.setCanSpeak(false);  // stays as a viewer, but cannot speak
            gameData.getAlivePlayers().remove(removedPlayer);
            gameData.getDeadPlayers().add(removedPlayer);
        }
    }

    /**
     * wakes up everyone and announces the night result
     */
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

    /**
     * Switches to chatroom mode and waits for 90 seconds until election day
     */
    public void chatMode()
    {
        wakeup(gameData.getAlivePlayers());
        sendMsgFromGod(GodMessages.chatroomMsg());
        waiting(80000);
        sendMsgFromGod("You've got 10 seconds left!");
        waiting(10000);
        sendMsgFromGod("Chatroom mode has ended!");
        sleep(gameData.getAlivePlayers());
    }

    /**
     * Starts an election and collects votes. Calculates the final candidate and asks mayor if they are okay with the election result
     * Kills the candidate if mayor said he was ok
     */
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

    /**
     * Announces the list of current dead and alive players
     */
    public void announcePlayersSate()
    {
        wakeup(gameData.getAlivePlayers());
        sendMsgFromGod("Alive players: " + gameData.getListOfAlivePlayers());
        sendMsgFromGod("Dead players: " + gameData.getListOfDeadPlayers());
        sleep(gameData.getAlivePlayers());
    }

    /**
     * Prepares the night before asking each player to perform their role
     */
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

    /**
     * Starts a mafia night by asking mafias about who they want to kill. Asks about the final decision from godfather
     */
    public void mafiaNight()
    {
        StringBuilder str = new StringBuilder();
        for (Player p : gameData.getMafias()) // calls each mafia's name
            str.append(p.getUsername()).append(" ");
        str.append(", who do you want to kill tonight?😈");
        if (gamePhase == NIGHT_MAFIA)
        {
            wakeup(gameData.getMafias());
            sendMsgFromGod(str.toString());
            collectVotes();
            godfatherDecision();
            sleep(gameData.getMafias());
        }
    }

    /**
     * Ends the night by saying goodbye to dead players and announcing the final result to all the players
     */
    public void endNight()
    {
        analyzeNightResult();
        announceNightResult();
        gameData.setLastNightResult(nightResult);
    }

    /**
     * Sleeps for the given amount of time
     * @param ms milliseconds
     */
    public void waiting(long ms)
    {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ends the game by shutting down the server
     * If the game has been played and has a winner, the winner will be announced to everyone
     */
    public void endGame()
    {
        Winner winner = gameData.getWinner();
        if (winner != Winner.UNKNOWN)
        {
            wakeup(gameData.getAlivePlayers());
            sendMsgFromGod("The game has ended\nThe winner is " + winner);
        }
        server.shutDownServer();
    }
}