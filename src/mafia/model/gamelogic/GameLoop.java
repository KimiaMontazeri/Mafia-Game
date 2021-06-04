package mafia.model.gamelogic;

import mafia.model.GameData;
import mafia.model.element.Winner;

public class GameLoop
{
    private final GameData gameData;
    private final GameManager god;

    public GameLoop()
    {
        gameData = GameData.getInstance();
        god = new GameManager();
    }

    public void start()
    {
        // if the game can be started
        if (god.setUpTheServer())
        {
            god.nextPhase(); // switches from "NOT_STARTED" to "INTRODUCTION_NIGHT"
            god.introduce(); // welcomes each player to the game and tell them their roles
            loop();
        }
        // the game is canceled, shut down the program
    }

    private void end()
    {
        // announce the winner to all the players and end the whole game
    }

    private void loop()
    {
        // check game over after each night phase or election day
        while (!gameOver())
        {
            /* mafia night and then check if the game is over or not
               wake up the mafias and close other's eyes */

            /* citizen night , open the eyes one oby one */

            /* announcing the night result and waking everyone up */

            /* day (chat mode, the god won't talk) */

            /* election day (Collecting the votes) */

            /* announcing the election result and then check if the game is over or not */
        }
        end();
    }

    private boolean gameOver()
    {
        int mafiaNum = gameData.getMafias().size();
        int citizenNum = gameData.getCitizens().size();

        if (mafiaNum >= citizenNum)
        {
            gameData.setWinner(Winner.MAFIA);
            return true;
        }
        if (mafiaNum == 0)
        {
            gameData.setWinner(Winner.CITY);
            return true;
        }
        return false;
    }
}
