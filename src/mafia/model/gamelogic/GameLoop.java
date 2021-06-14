package mafia.model.gamelogic;

import mafia.model.GameData;
import mafia.model.element.Winner;

/**
 * This class is the game's loop
 * It simply starts the game, switches to day mode and then to night mode until the game is over
 * @author KIMIA
 * @version 1.0
 */
public class GameLoop
{
    private final GameData gameData;
    private final GameManager god;

    /**
     * Creates a game loop
     */
    public GameLoop()
    {
        gameData = GameData.getInstance();
        god = new GameManager();
    }

    /**
     * Starts the game by launching the GameManager and switches to the introduction night (if the game starts)
     */
    public void start()
    {
        // if the game can be started
        if (god.launch())
        {
            god.nextPhase(); // switches from "NOT_STARTED" to "INTRODUCTION_NIGHT"
            god.introduce(); // welcomes each player to the game and tell them their roles
            loop();
        }
        // shut down the program
        god.endGame();
    }

    /**
     * The main loop of the game
     */
    private void loop()
    {
        while (!gameOver())
        {
            // day mode
            god.waiting(20000); // for the flow of the game
            god.announcePlayersSate();
            god.nextPhase();
            // all the alive and not silent players get to chat with each other for 90 seconds
            god.chatMode();
            god.nextPhase();
            // election time
            god.election();

            // night mode
            god.waiting(20000); // for the flow of the game
            god.announcePlayersSate();
            if (gameOver())
                break;
            god.nextPhase();

            god.prepareNight();
            // mafia night
            god.mafiaNight();
            god.nextPhase();
            // doctor lector night
            god.protect();
            god.nextPhase();
            // doctor night
            god.heal();
            god.nextPhase();
            // detective night
            god.detectPlayers();
            god.nextPhase();
            // sniper night
            god.shoot();
            god.nextPhase();
            // therapist night
            god.silent();
            god.nextPhase();
            // arnold night
            god.inquiry();

            god.endNight();
        }
    }

    /**
     * Checks if the game is over or not
     * @return true if the game is over
     */
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
