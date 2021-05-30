package mafia.model.element;

public class Player
{
    private boolean isMafia;  // used for counting the mafias and citizens
    private boolean isAlive;
    private boolean canSpeak; // used for when the therapist shushes a player
    private boolean readyToPlay;
    private final String username;
    private final Role role;

    public Player(boolean isMafia, String username, Role role)
    {
        this.isMafia = isMafia;
        this.username = username;
        this.role = role;
        readyToPlay = false;
    }

    public boolean isMafia() {
        return isMafia;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean canSpeak() {
        return canSpeak;
    }

    public void isReadyToPlay()
    {
        readyToPlay = true;
        isAlive = true;
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

    public void setCanSpeak(boolean canSpeak) {
        this.canSpeak = canSpeak;
    }
}
