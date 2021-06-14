package mafia.model.element;

import static mafia.model.element.Role.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class store the night results of the mafia game such as all the murders, ...
 * @author KIMIA
 * @version 1.0
 */
public class NightResult
{
    private final HashMap<Player, Role> murders; // killed player -> murderer
    private final HashMap<Player, Role> heals; // healed player -> normal doctor/doctor lector
    private final Set<Player> removedPlayers;
    private boolean arnoldHadInquiry = false;
    private Player silencedPlayer;

    /**
     * Creates a night result
     */
    public NightResult() {
        murders = new HashMap<>();
        heals = new HashMap<>();
        removedPlayers = new HashSet<>();
    }

    /**
     * @return murders
     */
    public HashMap<Player, Role> getMurders() {
        return murders;
    }

    /**
     * @return heals
     */
    public HashMap<Player, Role> getHeals() {
        return heals;
    }

    /**
     * @return players who got removed from the game at night
     */
    public Set<Player> getRemovedPlayers() {
        return removedPlayers;
    }

    /**
     * @return whether arnold had a inquiry at night or not
     */
    public boolean arnoldHadInquiry() {
        return arnoldHadInquiry;
    }

    /**
     * @return Player who has been silenced by the therapist at night
     */
    public Player getSilencedPlayer() {
        return silencedPlayer;
    }

    /**
     * @param arnoldHadInquiry whether arnold had an inquiry at night or not
     */
    public void setArnoldHadInquiry(boolean arnoldHadInquiry) {
        this.arnoldHadInquiry = arnoldHadInquiry;
    }

    /**
     * Sets the silenced player to the given parameter
     * @param silencedPlayer player who has been silenced
     */
    public void setSilencedPlayer(Player silencedPlayer) {
        this.silencedPlayer = silencedPlayer;
    }

    /**
     * Checks if the player has been protected by doctor lector
     * @param player player to check
     * @return true if the player is protected by lector
     */
    public boolean isProtectedByLector(Player player)
    {
        if (heals.containsKey(player))
            return heals.get(player) == LECTOR;
        return false;
    }

    /**
     * Adds a murder to the list of the night's murders
     * @param killedPlayer killed player
     * @param murderer murderer
     */
    public void addMurder(Player killedPlayer, Role murderer)
    {
        if (murderer == MAFIA || murderer == GODFATHER)
            murders.put(killedPlayer, murderer);
        else if (murderer == SNIPER)
        {
            if (!heals.containsKey(killedPlayer)) // sniper won't be able to kill a protected mafia
                murders.put(killedPlayer, murderer);
        }
    }

    /**
     * Adds a heal to the list
     * @param healedPlayer player who has been healed
     * @param healer healer (it can be a doctor or the mafia's doctor-doctor lector)
     */
    public void addHeal(Player healedPlayer, Role healer)
    {
        if (murders.containsKey(healedPlayer) && healer == DOCTOR)
        {
            murders.remove(healedPlayer);
            heals.put(healedPlayer, healer);
        }
        else if (healer == LECTOR)
            heals.put(healedPlayer, healer);
    }

    /**
     * Adds a player to the list of the night's removed players
     * @param player player who has been removed
     */
    public void addRemovedPlayer(Player player) {
        removedPlayers.add(player);
    }

    /**
     *
     * @return All the night events in a string
     */
    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        if (murders.size() == 0 && removedPlayers.size() == 0)
            str.append("Nobody got killed last night :|");

        else
        {
            str.append("We found the body/bodies of [ ");
            for (Player killedPlayer : murders.keySet())
                str.append(killedPlayer.getUsername()).append(" ");
            for (Player killedPlayer : removedPlayers)
                str.append(killedPlayer.getUsername()).append(" ");
            str.append("] last night 😲");
        }
        if (silencedPlayer != null)
            str.append("\n").append(silencedPlayer.getUsername()).append(" got silenced for the next day");
        return str.toString();
    }
}
