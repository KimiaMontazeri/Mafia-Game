package mafia.model.element;

public class Client
{
    private Player player;

    public void connect()
    {
        // TODO: create a socket and connect to the server then server asks the client's username

        while (player.isAlive())
        {
            // TODO: open input/output streams and wait for "appropriate" messages
        }

        // TODO: every messages are visible to the client because they are dead
    }
}
