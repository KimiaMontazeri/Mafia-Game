package mafia;

import mafia.model.gamelogic.GameLoop;

/**
 * Contains the main method for the server side of the program
 * @author KIMIA
 * @version 1.0
 */
public class ServerMain
{
    /**
     * Starts the server side of the program
     */
    public static void main(String[] args)
    {
        GameLoop gameLoop = new GameLoop();
        gameLoop.start();
    }
}
