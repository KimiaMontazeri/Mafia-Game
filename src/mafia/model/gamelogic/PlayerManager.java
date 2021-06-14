package mafia.model.gamelogic;

import mafia.model.GameData;
import mafia.model.element.Player;
import mafia.model.element.Role;

import java.util.HashSet;

/**
 * This class manages the players, eg waking up a set of players or putting them to sleep
 * It also handles removing roles from the game
 * @author KIMIA
 * @version 1.0
 */
public class PlayerManager
{
    private static final GameData gameData = GameData.getInstance();

    /**
     * Wakes up the given players
     * @param players a set of players
     */
    public static void wakeup(HashSet<Player> players)
    {
        if (players != null)
        {
            for (Player p : players)
                p.wakeup();
        }
    }

    /**
     * Puts the given players to bed
     * @param players a set of players
     */
    public static void sleep(HashSet<Player> players)
    {
        if (players != null)
        {
            for (Player p : players)
                p.goToSleep();
        }
    }

    /**
     * Wakes up the given role
     * @param role a role
     */
    public static void wakeup(Role role)
    {
        HashSet<Player> players = gameData.getAlivePlayers();
        for (Player p : players)
        {
            if (p.getRole() == role)
                p.wakeup();
        }
    }

    /**
     * Puts the given role to sleep
     * @param role a role
     */
    public static void sleep(Role role)
    {
        HashSet<Player> players = gameData.getAlivePlayers();
        for (Player p : players)
        {
            if (p.getRole() == role)
                p.goToSleep();
        }
    }

    /**
     * Sets the boolean field (that store if the game has the removed player's role) to false
     * @param removedPlayer player who has been removed from the game
     */
    public static void handlePlayerRemoval(Player removedPlayer)
    {
        switch (removedPlayer.getRole())
        {
            case DOCTOR ->        gameData.hasDoctor = false;
            case DETECTIVE ->     gameData.hasDetective = false;
            case MAYOR ->         gameData.hasMayor = false;
            case ARNOLD ->        gameData.hasArnold = false;
            case SNIPER ->        gameData.hasSniper = false;
            case THERAPIST ->     gameData.hasTherapist = false;
            case GODFATHER ->     gameData.hasGodfather = false;
            case LECTOR ->        gameData.hasLector = false;
        }
    }
}
