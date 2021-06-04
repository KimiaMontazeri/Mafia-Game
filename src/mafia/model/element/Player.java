package mafia.model.element;

import java.util.Objects;

public class Player
{
    private boolean isAlive;
    private boolean asleep;
    private boolean canSpeak; // used for when the therapist shushes a player
    private final String username;
    private Role role;

    public Player(String username)
    {
        this.username = username;
        asleep = false;
        role = Role.UNKNOWN;
    }

    public boolean isAlive() {
        return isAlive;
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

    public void getKilled() {
        isAlive = false;
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
}
