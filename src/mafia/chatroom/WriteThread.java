package mafia.chatroom;

import mafia.model.element.Message;
import mafia.model.element.Role;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class WriteThread extends Thread
{
    private ObjectOutputStream objectOutputStream;
    private Socket socket;
    private final Client client;
    private final String username;

    public WriteThread(Socket socket, Client client)
    {
        this.socket = socket;
        this.client = client;
        username = client.getPlayer().getUsername();
    }

    public void openStreams()
    {
        try
        {
            OutputStream out = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(out);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        openStreams();
        Scanner scanner = new Scanner(System.in);
        while (true)
        {
            try
            {
                String text;

                do
                {
                    text = scanner.nextLine();
                    // TODO define a method that determines the receivers of this client's message (with the help of the server and game's state)
                    Message msg = new Message(text, client.getPlayer().getUsername(), Role.UNKNOWN);
                    objectOutputStream.writeObject(msg);

                } while (client.getPlayer().isAlive());
            }
            catch (IOException e)
            {
                System.err.println("Error sending your message to the server :(");
                break;
            }
        }
    }
}
