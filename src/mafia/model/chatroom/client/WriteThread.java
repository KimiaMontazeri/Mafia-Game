package mafia.model.chatroom.client;


import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * This class is a thread that handles writing to the socket's output stream
 * @author KIMIA
 * @version 1.0
 */
public class WriteThread extends Thread
{
    private final DataOutputStream dataOutputStream;
    private final Socket socket;

    /**
     *
     * @param socket socket to connect to the server
     * @param dataOutputStream output stream of the socket
     */
    public WriteThread(Socket socket, DataOutputStream dataOutputStream)
    {
        this.dataOutputStream = dataOutputStream;
        this.socket = socket;
    }

    /**
     * Starts the thread and waits for the user to type something and sends it to the server
     * This thread keeps waiting for user's commands until they type "exit"
     */
    @Override
    public void run()
    {
        Scanner scanner = new Scanner(System.in);
        try
        {
            String text = "";

            while (!text.equals("exit"))
            {
                text = scanner.nextLine();
                dataOutputStream.writeUTF(text);
            }

        } catch (SocketException e) {
            System.err.println("Got disconnected from the game");
        } catch (IOException e) {
            System.err.println("I/O error occurred");
        } finally {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                System.err.println("Could not close output stream");
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
