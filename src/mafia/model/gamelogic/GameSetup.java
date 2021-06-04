package mafia.model.gamelogic;

import mafia.model.GameData;
import mafia.model.element.Player;
import static mafia.model.element.Role.*;
import static mafia.model.element.Phase.*;

import java.util.*;

public class GameSetup
{
    private static GameData gameData;

    /**
     * Initializes the game after all the players are ready
     * The GameManager calls this method to start the game
     */
    public static void initialize(ArrayList<String> usernames) {
        gameData = GameData.getInstance();
        gameData.setAlivePlayers(playersInit(usernames));
        gameData.setCurrentMood(NOT_STARTED);
    }

    private static ArrayList<Player> playersInit(ArrayList<String> usernames)
    {
        ArrayList<Player> players = new ArrayList<>();

        for (String username : usernames)
            players.add(new Player(username));

        // generate random roles for each player
        int totalPlayers = players.size();
        int mafiaNum = totalPlayers / 3;
        int citizenNum = totalPlayers - mafiaNum;

        Random random = new Random();
        int randomNumber;
        Player chosenPlayer;

        // dividing the players into to 2 teams -> mafias and citizens
        ArrayList<Player> mafias = new ArrayList<>();
        ArrayList<Player> citizens = new ArrayList<>();

        for (int i = 0; i < mafiaNum; i++)
        {
            randomNumber = random.nextInt(totalPlayers);
            chosenPlayer = players.get(randomNumber);
            if (chosenPlayer.getRole() == UNKNOWN)
            {
                chosenPlayer.setRole(MAFIA);
                mafias.add(chosenPlayer); // remove kon -_-
                System.out.println("Added " + chosenPlayer.getUsername() + " to team mafia");
            }
            else i--;
        }
        for (Player p : players)
        {
            if (p.getRole() == UNKNOWN)
            {
                p.setRole(CITIZEN);
                citizens.add(p);
                System.out.println("Added " + p.getUsername() + " to team city");
            }
        }

        // setting mafia roles to the mafias
        mafias.get(0).setRole(GODFATHER);
        System.out.println(mafias.get(0).getUsername() + " is godfather");
        if (mafiaNum > 3)
        {
            mafias.get(1).setRole(LECTOR);
            gameData.hasLector = true;
            System.out.println(mafias.get(1).getUsername() + " is doctor lector");
        }

        // setting citizen roles to the citizens
        citizens.get(0).setRole(DOCTOR);
        System.out.println(citizens.get(0).getUsername() + " is doctor");
        citizens.get(1).setRole(DETECTIVE);
        System.out.println(citizens.get(1).getUsername() + " is detective");
        if (citizenNum > 5)
        {
            citizens.get(2).setRole(MAYOR);
            System.out.println(citizens.get(2).getUsername() + " is mayor");
            citizens.get(3).setRole(SNIPER);
            System.out.println(citizens.get(3).getUsername() + " is sniper");
            citizens.get(4).setRole(ARNOLD);
            System.out.println(citizens.get(4).getUsername() + " is arnold");
            citizens.get(5).setRole(THERAPIST);
            System.out.println(citizens.get(5).getUsername() + " is therapist");
            gameData.hasMayor = true;
            gameData.hasSniper = true;
            gameData.hasArnold = true;
            gameData.hasTherapist = true;
        }
        else if (citizenNum == 5)
        {
            citizens.get(2).setRole(SNIPER);
            System.out.println(citizens.get(2).getUsername() + " is sniper");
            citizens.get(3).setRole(ARNOLD);
            System.out.println(citizens.get(3).getUsername() + " is arnold");
            citizens.get(4).setRole(THERAPIST);
            System.out.println(citizens.get(4).getUsername() + " is therapist");
            gameData.hasSniper = true;
            gameData.hasArnold = true;
            gameData.hasTherapist = true;
        }
        else if (citizenNum == 4)
        {
            citizens.get(2).setRole(SNIPER);
            System.out.println(citizens.get(2).getUsername() + " is sniper");
            citizens.get(3).setRole(ARNOLD);
            System.out.println(citizens.get(3).getUsername() + " is arnold");
            gameData.hasSniper = true;
            gameData.hasArnold = true;
        }
        // the game won't have sniper arnold mayor or therapist if the number of the citizens is less than 4

        return players;
    }
}
