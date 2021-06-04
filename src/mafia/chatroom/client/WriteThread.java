package mafia.chatroom.client;

import mafia.model.element.Message;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class WriteThread extends Thread
{
    private ObjectOutputStream objectOutputStream;
    private Socket socket;
    private final String username;

    public WriteThread(Socket socket, String username)
    {
        this.socket = socket;
        this.username = username;
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
                    Message msg = new Message(text, username);
                    objectOutputStream.writeObject(msg);

                } while (!text.equals("exit")); // user won't be able to send messages if the exit the game
            }
            catch (SocketException e) {
                System.err.println("CONNECTION FAILED");
                break;
            }
            catch (IOException e) {
                System.err.println("Error sending your message to the server :(");
            }
        }
    }
}
