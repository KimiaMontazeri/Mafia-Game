package mafia;

import mafia.model.gamelogic.GameLoop;

public class ServerMain
{
    public static void main(String[] args)
    {
        GameLoop gameLoop = new GameLoop();
        gameLoop.start();
    }
}
