package mafia.model.chatroom.client;

import mafia.view.Display;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * This class is a thread that handles reading from the socket's input stream
 * @author KIMIA
 * @version 1.0
 */
public class ReadThread extends Thread
{
    private final DataInputStream dataInputStream;
    private final Socket socket;

    /**
     *
     * @param socket socket to connect to the server
     * @param dataInputStream input stream of the socket
     */
    public ReadThread(Socket socket, DataInputStream dataInputStream)
    {
        this.socket = socket;
        this.dataInputStream = dataInputStream;
    }

    /**
     * Starts the thread and waits for a message to be sent from server through the input stream
     */
    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                String text = dataInputStream.readUTF();
                if (text.contains("HISTORY"))
                    Display.displayHistory(text);
                else if (text.equals("DISCONNECT"))
                    break;
                else
                    Display.print(text);
            }

        } catch (SocketException e) {
            System.err.println("Got disconnected from the game");
        } catch (IOException e) {
            System.err.println("I/O error occurred");
        } finally {
            try {
                dataInputStream.close();
            } catch (IOException e) {
                System.err.println("Could not close input stream");
            } finally {
                try {
                    if (socket.isConnected()) socket.close();
                } catch (IOException e) {
                    System.err.println("Could not close socket");
                }
            }
        }
    }
}
