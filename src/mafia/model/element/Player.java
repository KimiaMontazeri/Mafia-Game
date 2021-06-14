package mafia.model.element;

import java.util.Objects;

/**
 * This class represents a player in mafia game
 * @author KIMIA
 * @version 1.0
 */
public class Player
{
    private boolean asleep;
    private boolean canSpeak; // used for when the therapist silents a player or player stays as a viewer in the game
    private final String username;
    private Role role;

    /**
     * Creates a player
     * @param username player's username
     */
    public Player(String username)
    {
        this.username = username;
        asleep = false;
        canSpeak = true;
        role = Role.UNKNOWN;
    }

    /**
     * @return if the player is asleep
     */
    public boolean isAsleep() {
        return asleep;
    }

    /**
     * @return if the player can speak
     */
    public boolean canSpeak() {
        return canSpeak;
    }

    /**
     * @return player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return player's role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the player's role
     * @param role role
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Player goes to sleep
     */
    public void goToSleep() {
        asleep = true;
    }

    /**
     * Player wakes up
     */
    public void wakeup() {
        asleep = false;
    }

    /**
     * Sets canSpeak to the given boolean
     * @param canSpeak whether the player can speak
     */
    public void setCanSpeak(boolean canSpeak) {
        this.canSpeak = canSpeak;
    }

    /**
     * Checks if this player is equal to the given parameter or not
     * @param o object to check its equality with this player
     * @return true if they are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return getUsername().equals(player.getUsername());
    }

    /**
     * @return a hashcode for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getRole());
    }

    /**
     * @return player's username
     */
    @Override
    public String toString() {
        return username;
    }
}
