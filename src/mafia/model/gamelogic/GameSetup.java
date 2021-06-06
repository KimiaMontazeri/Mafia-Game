package mafia.model.gamelogic;

import mafia.model.GameData;
import mafia.model.element.Player;
import static mafia.model.element.Role.*;
import static mafia.model.element.Phase.*;

import java.util.*;

public class GameSetup
{
    private GameData gameData;

    public GameSetup() {
        gameData = GameData.getInstance();
    }

    /**
     * Initializes the game after all the players are ready
     * The GameManager calls this method to start the game
     */
    public void initialize(ArrayList<String> usernames) {
        gameData = GameData.getInstance();
        gameData.setAlivePlayers(playersInit(usernames));
        gameData.setCurrentPhase(NOT_STARTED);
    }

    private ArrayList<Player> playersInit(ArrayList<String> usernames)
    {
        ArrayList<Player> players = new ArrayList<>(usernames.size());

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
        ArrayList<Player> mafias = new ArrayList<>(mafiaNum);
        ArrayList<Player> citizens = new ArrayList<>(citizenNum);

        for (int i = 0; i < mafiaNum; i++)
        {
            randomNumber = random.nextInt(totalPlayers);
            chosenPlayer = players.get(randomNumber);
            if (chosenPlayer.getRole() == UNKNOWN)
            {
                chosenPlayer.setRole(MAFIA);
                mafias.add(chosenPlayer); // remove kon -_-
            }
            else i--;
        }
        for (Player p : players)
        {
            if (p.getRole() == UNKNOWN)
            {
                p.setRole(CITIZEN);
                citizens.add(p);
            }
        }

        // setting mafia roles to the mafias
        mafias.get(0).setRole(GODFATHER);
        if (mafiaNum > 3)
        {
            mafias.get(1).setRole(LECTOR);
            gameData.hasLector = true;
        }

        // setting citizen roles to the citizens
        citizens.get(0).setRole(DOCTOR);
        citizens.get(1).setRole(DETECTIVE);
        if (citizenNum > 6)
        {
            citizens.get(2).setRole(MAYOR);
            citizens.get(3).setRole(SNIPER);
            citizens.get(4).setRole(ARNOLD);
            citizens.get(5).setRole(THERAPIST);
            gameData.hasMayor = true;
            gameData.hasSniper = true;
            gameData.hasArnold = true;
            gameData.hasTherapist = true;
        }
        else if (citizenNum == 6)
        {
            citizens.get(2).setRole(SNIPER);
            citizens.get(3).setRole(ARNOLD);
            citizens.get(4).setRole(THERAPIST);
            gameData.hasSniper = true;
            gameData.hasArnold = true;
            gameData.hasTherapist = true;
        }
        else if (citizenNum == 5)
        {
            citizens.get(2).setRole(SNIPER);
            citizens.get(3).setRole(ARNOLD);
            gameData.hasSniper = true;
            gameData.hasArnold = true;
        }
        // the game won't have sniper arnold mayor or therapist if the number of the citizens is less than 4
        gameData.hasGodfather = gameData.hasDoctor = gameData.hasDetective = true;
        return players;
    }
}
