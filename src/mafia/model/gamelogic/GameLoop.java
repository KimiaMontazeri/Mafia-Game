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
        if (god.launch())
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
        god.wakeup(gameData.getAlivePlayers());
        god.sendMsgFromGod("The game has ended\nThe winner is " + gameData.getWinner());
    }

    private void loop()
    {
        // check game over after each night phase or election day
        while (!gameOver())
        {
            god.waiting(20000); // for the flow of the game
            god.nextPhase();
            god.doNightActs();

            if (gameOver())
                break;

            god.waiting(20000); // for the flow of the game
            god.nextPhase();
            god.doDayActs();
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
