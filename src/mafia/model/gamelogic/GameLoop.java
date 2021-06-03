package mafia.model.gamelogic;

import mafia.model.GameData;
import mafia.model.element.Winner;

public class GameLoop
{
    private GameData gameData;
    private GameManager god;

    public GameLoop()
    {
        gameData = GameData.getInstance();
        god = new GameManager();
    }

    public void start()
    {
        // calls the game manager setUpTheServer method

        // welcoming the players by telling them their roles (introduction night)
        god.nextPhase(); // switches from "NOT_STARTED" to "INTRODUCTION_NIGHT"
        god.introduce();
    }

    public void end()
    {
        // announce the winner to all the players and end the whole game
    }

    public void loop()
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
    }

    public boolean gameOver()
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
