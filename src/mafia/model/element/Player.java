package mafia.model.element;

import java.util.Objects;

public class Player
{
    private boolean asleep;
    private boolean canSpeak; // used for when the therapist silents a player or player gets removed from the game
    private final String username;
    private Role role;

    public Player(String username)
    {
        this.username = username;
        asleep = false;
        canSpeak = true;
        role = Role.UNKNOWN;
    }

    public boolean isAsleep() {
        return asleep;
    }

    public boolean canSpeak() {
        return canSpeak;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void goToSleep() {
        asleep = true;
    }

    public void wakeup() {
        asleep = false;
    }

    public void setCanSpeak(boolean canSpeak) {
        this.canSpeak = canSpeak;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return getUsername().equals(player.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getRole());
    }

    @Override
    public String toString() {
        return username;
    }
}
