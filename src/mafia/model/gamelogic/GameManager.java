package mafia.model.gamelogic;

import mafia.chatroom.server.Server;
import mafia.model.GameData;
import mafia.model.element.*;
import static mafia.model.element.Phase.*;
import static mafia.model.element.Role.*;
import static mafia.model.GodMessages.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GameManager // rules the game, gives the client handler the permission to send particular messages
{
    private Server server;
    private final GameData gameData;
    private String gameID; // as a parameter of the constructor
    private Phase gamePhase;

    private boolean electionIsOn = false;
    private final Set<Vote> votes;
    private Player lastKilledPlayer; //  or player's'

    public GameManager()
    {
        server = new Server(5757);
        gameData = GameData.getInstance();
        gamePhase = NOT_STARTED;
        votes = new HashSet<>();
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

    public void wakeupRole(Role role)
    {
        HashSet<Player> players = gameData.getAlivePlayers();
        for (Player p : players)
        {
            if (p.getRole() == role)
                p.wakeup();
        }
    }

    public void sleepRole(Role role)
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
            case NOT_STARTED ->          gamePhase = INTRODUCTION_NIGHT;
            case INTRODUCTION_NIGHT ->   gamePhase = DAY;
            case DAY ->                  gamePhase = ELECTION_DAY;
            case ELECTION_DAY ->         gamePhase = DAY_MAYOR;
            case DAY_MAYOR ->            gamePhase = NIGHT_MAFIA;
            case NIGHT_MAFIA ->          gamePhase = NIGHT_LECTOR;
            case NIGHT_LECTOR ->         gamePhase = NIGHT_DOCTOR;
            case NIGHT_DOCTOR ->         gamePhase = NIGHT_DETECTIVE;
            case NIGHT_DETECTIVE ->      gamePhase = NIGHT_SNIPER;
            case NIGHT_SNIPER ->         gamePhase = NIGHT_THERAPIST;
            case NIGHT_THERAPIST ->      gamePhase = NIGHT_ARNOLD;
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
            wakeupRole(GODFATHER);
            sendMsgFromGod(welcomeGodfather());
            sleepRole(GODFATHER);

            // introduce doctor lector
            if (gameData.hasLector)
            {
                wakeupRole(LECTOR);
                sendMsgFromGod(welcomeLector());
                sleepRole(LECTOR);
            }

            // introduce the normal citizens
            wakeupRole(CITIZEN);
            sendMsgFromGod(welcomeCitizens());
            sleepRole(CITIZEN);

            // introduce the city's doctor
            wakeupRole(DOCTOR);
            sendMsgFromGod(welcomeDoctor());
            sleepRole(DOCTOR);

            // introduce the doctor to the mayor
            if (gameData.hasMayor)
            {
                wakeupRole(MAYOR);
                sendMsgFromGod(welcomeMayor(findPlayer(DOCTOR)));
                sleepRole(MAYOR);
            }

            // detective
            wakeupRole(DETECTIVE);
            sendMsgFromGod(welcomeDetective());
            sleepRole(DETECTIVE);

            // sniper
            if (gameData.hasSniper)
            {
                wakeupRole(SNIPER);
                sendMsgFromGod(welcomeSniper());
                sleepRole(SNIPER);
            }

            // therapist
            if (gameData.hasTherapist)
            {
                wakeupRole(THERAPIST);
                sendMsgFromGod(welcomeTherapist());
                sleepRole(THERAPIST);
            }

            // arnold
            if (gameData.hasArnold)
            {
                wakeupRole(ARNOLD);
                sendMsgFromGod(welcomeArnold());
                sleepRole(ARNOLD);
            }
        }
    }

    public Player findPlayer(Role role)
    {
        for (Player p : gameData.getAlivePlayers())
        {
            if (p.getRole() == role)
                return p;
        }
        return null;
    }

}
