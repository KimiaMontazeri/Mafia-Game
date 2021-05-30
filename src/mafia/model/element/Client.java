package mafia.model.element;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    private Player player;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private Scanner scanner;

    public void connect()
    {
        // TODO: create a socket and connect to the server then server asks the client's username

        while (player.isAlive())
        {
            // TODO: open input/output streams and wait for "appropriate" messages
        }

        // TODO: every messages are visible to the client because they are dead
    }

    public void closeInputStream()
    {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeOutputStream()
    {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openInputStream()
    {

    }

    public void openOutputStream()
    {

    }
}
