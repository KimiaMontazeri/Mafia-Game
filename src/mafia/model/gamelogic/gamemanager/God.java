package mafia.model.gamelogic.gamemanager;

import mafia.model.element.Message;
import mafia.model.element.Player;
import mafia.model.element.Role;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;

public class God
{
    private ServerSocket serverSocket;
    private OutputStream out;
    private InputStream in;

    public void connectToClients()
    {
        // TODO open a socket and wait for all the players to connect
    }

    public void sendMessage(String text, Player sender, Role... receivers)
    {
        // an example of what this method has to do
        try
        {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
            objectOutputStream.writeObject(new Message(text, sender, receivers));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }
}
