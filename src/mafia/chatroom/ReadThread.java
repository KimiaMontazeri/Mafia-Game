package mafia.chatroom;

import mafia.model.element.Message;
import mafia.view.Display;

import java.io.*;
import java.net.Socket;

public class ReadThread extends Thread
{
    private ObjectInputStream objectInputStream;
    private Socket socket;
    private final Client client;
    private final String username;

    public ReadThread(Socket socket, Client client)
    {
        this.socket = socket;
        this.client = client;
        username = client.getPlayer().getUsername();
    }

    public void openStreams()
    {
        try
        {
            InputStream input = socket.getInputStream();
            objectInputStream = new ObjectInputStream(input);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        openStreams();
        while (true)
        {
            try
            {
                Message receivedMsg = (Message) objectInputStream.readObject();
                // show the message to the client, if they are one of the receivers
                // TODO if statement below may be modified later
                if (receivedMsg.getReceivers().contains(client.getPlayer().getRole()))
                {
                    Display.print(receivedMsg);

                    // TODO if statement below may be modified later
                    // prints the username after displaying the server's message
                    if (username != null) {
                        Display.print("[" + username + "]: ");
                    }
                }
            }
            catch (IOException | ClassNotFoundException e)
            {
                System.err.println("Error reading from server :(");
                break;
            }
        }
    }
}
