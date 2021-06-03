package mafia.model.gamelogic;

import mafia.model.GameData;
import mafia.model.element.Player;
import mafia.model.element.Role;

import java.util.*;

public class GameSetup
{
    /**
     * Initializes the game after all the players are ready
     * The GameManager calls this method to start the game
     */
    public static void initialize(ArrayList<String> usernames, GameData gameData) {
        gameData.setAlivePlayers(playersInit(usernames, gameData));
    }

    private static ArrayList<Player> playersInit(ArrayList<String> usernames, GameData gameData)
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
            if (chosenPlayer.getRole() == Role.UNKNOWN)
            {
                chosenPlayer.setRole(Role.MAFIA);
                mafias.add(chosenPlayer);
            }
            else i--;
        }
        for (Player p : players)
        {
            if (p.getRole() == Role.UNKNOWN)
                citizens.add(p);
        }

        // setting mafia roles to the mafias
        mafias.get(0).setRole(Role.GODFATHER);
        if (mafiaNum > 3)
        {
            mafias.get(1).setRole(Role.LECTOR);
            gameData.hasLector = true;
        }

        // setting citizen roles to the citizens
        citizens.get(0).setRole(Role.DOCTOR);
        citizens.get(1).setRole(Role.DETECTIVE);
        if (citizenNum > 5)
        {
            mafias.get(2).setRole(Role.MAYOR);
            mafias.get(3).setRole(Role.SNIPER);
            mafias.get(4).setRole(Role.ARNOLD);
            mafias.get(5).setRole(Role.THERAPIST);
            gameData.hasMayor = true;
            gameData.hasSniper = true;
            gameData.hasArnold = true;
            gameData.hasTherapist = true;
        }
        else if (citizenNum == 5)
        {
            mafias.get(2).setRole(Role.SNIPER);
            mafias.get(3).setRole(Role.ARNOLD);
            mafias.get(4).setRole(Role.THERAPIST);
            gameData.hasSniper = true;
            gameData.hasArnold = true;
            gameData.hasTherapist = true;
        }
        else if (citizenNum == 4)
        {
            mafias.get(2).setRole(Role.SNIPER);
            mafias.get(3).setRole(Role.ARNOLD);
            gameData.hasSniper = true;
            gameData.hasArnold = true;
        }
        // the game won't have sniper arnold mayor or therapist if the number of the citizens is less than 4

        return players;
    }
}
