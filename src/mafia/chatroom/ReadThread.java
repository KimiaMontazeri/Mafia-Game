package mafia.chatroom;

import mafia.model.element.Message;
import mafia.view.Display;

import java.io.*;
import java.net.Socket;

public class ReadThread extends Thread
{
    private ObjectInputStream objectInputStream;
    private Socket socket;
    private final String username;

    public ReadThread(Socket socket, String username)
    {
        this.socket = socket;
        this.username = username;
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
                Display.print(receivedMsg);

                // if statement below may be modified later
                // prints the username after displaying the server's message
                // indicates that an answer is expected from the client
                if (username != null) {
                    Display.print("[" + username + "]: ");
                }

                // break from the loop if the game is ended
            }
            catch (IOException | ClassNotFoundException e) {
                System.err.println("Error reading from server :(");
            }
        }
    }
}
