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
    private Phase gamePhase;
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
            case INTRODUCTION_NIGHT, NIGHT_ARNOLD ->  gamePhase = DAY;
            case DAY ->                               gamePhase = ELECTION_DAY;
            case ELECTION_DAY ->                      gamePhase = DAY_MAYOR;
            case DAY_MAYOR ->                         gamePhase = NIGHT_MAFIA;
            case NIGHT_MAFIA ->                       gamePhase = NIGHT_DOCTOR;
            case NIGHT_DOCTOR ->                      gamePhase = NIGHT_DETECTIVE;
            case NIGHT_DETECTIVE ->                   gamePhase = NIGHT_SNIPER;
            case NIGHT_SNIPER ->                      gamePhase = NIGHT_LECTOR;
            case NIGHT_LECTOR ->                      gamePhase = NIGHT_THERAPIST;
            case NIGHT_THERAPIST ->                   gamePhase = NIGHT_ARNOLD;
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

    public Player election()
    {
        if (gamePhase == NIGHT_MAFIA)
        {
            sendMsgFromGod("choose from these users");
            server.startElection();
            // TODO ask godfather to make the final decision

            // some code ...

            // final steps:
            Election election = gameData.getLastElection();
            Player candidate = gameData.findPlayer(election.calFinalResult());
            nightResult.addMurder(candidate, MAFIA);
            return candidate;
        }
        else if (gamePhase == ELECTION_DAY)
        {
            sendMsgFromGod("choose from these users");
            server.startElection();

            Election election = gameData.getLastElection();
            return gameData.findPlayer(election.calFinalResult());
        }
        return null;
    }

    public void kill()
    {

    }

    public void heal()
    {

    }

    public void detectPlayers()
    {

    }

    public void silent()
    {

    }

    public void inquiry()
    {

    }

    public boolean cancelElection(Player candidate)
    {
        if (gamePhase == DAY_MAYOR)
        {
            wakeup(MAYOR);
            // TODO some code
            sleep(MAYOR);
            return true;
        }
        return false;
    }

    public void kickOut(Player candidate)
    {
        // TODO complete this method
        gameData.getAlivePlayers().remove(candidate);
        gameData.getDeadPlayers().add(candidate);
    }

    public void doNightActs()
    {
        if (gamePhase == NIGHT_MAFIA)
        {
            wakeup(gameData.getMafias());
            election();
            kill();
            sleep(gameData.getMafias());
        }
        else return;
        nextPhase();

        wakeup(DOCTOR);
        heal();
        sleep(DOCTOR);
        nextPhase();

        wakeup(DETECTIVE);
        detectPlayers();
        sleep(DETECTIVE);
        nextPhase();

        wakeup(SNIPER);
        kill();
        sleep(SNIPER);
        nextPhase();

        if (nightResult.hasSniperKill())
        {
            wakeup(LECTOR);
            heal();
            sleep(LECTOR);
        }
        nextPhase();

        wakeup(THERAPIST);
        silent();
        sleep(THERAPIST);
        nextPhase();

        wakeup(ARNOLD);
        inquiry();
        sleep(ARNOLD);

        wakeup(gameData.getAlivePlayers());
        sendMsgFromGod(nightResult.toString()); // or call announceNightResult() 
        sleep(gameData.getAlivePlayers());
    }

    public void announceNightResult() {
        sendMsgFromGod(nightResult.toString());
    }

    public void doDayActs()
    {
        wakeup(gameData.getAlivePlayers());
        sendMsgFromGod("""
                CHATROOM mode
                Discuss whoever you think might have to be removed from the game...
                Note that you only have 90 seconds until the election time!
                """);
        try {
            Thread.sleep(90000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Player candidate = election();
        // if the election didn't get canceled by the mayor, kick out the candidate from the game
        sleep(gameData.getAlivePlayers());
        if (!cancelElection(candidate))
        {
            // wake up the person who has to get kicked out (is the returned variable of election)
            kickOut(candidate); // this method is called if the election has not got canceled
            wakeup(gameData.getAlivePlayers());
            // TODO notify the players that the candidate got removed from the game
        }
        wakeup(gameData.getAlivePlayers());
        // TODO notify the players that the election is canceled
        sleep(gameData.getAlivePlayers());
    }
}
