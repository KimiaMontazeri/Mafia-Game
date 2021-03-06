package mafia.model.chatroom.client;

import mafia.view.Display;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * Game's client gets connected to the game and starts to play
 * @author KIMIA
 * @version 1.0
 */
public class Client
{
    private final int port;

    /**
     * Gets the port from the user by calling the appropriate method
     * and sets it to the field "port"
     */
    public Client() {
        port = getPortFromUser();
    }

    /**
     * user types in the game's port to get connected
     * @return port
     */
    private int getPortFromUser()
    {
        Scanner scanner = new Scanner(System.in);
        while (true)
        {
            Display.print("Enter the game server's port: ");
            try {
                int port = Integer.parseInt(scanner.nextLine());
                if (port == 5757)
                    return port;
            } catch (NumberFormatException e) {
                Display.print("Wrong input!");
            }
        }
    }

    /**
     * connects to the server by creating a socket
     */
    public void connectToServer()
    {
        try
        {
            // server's data
            Socket socket = new Socket("127.0.0.1", port);
            Display.print("Connected to the game!\n");

            // streams
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            // directed to the game
            new WriteThread(socket, dataOutputStream).start();
            new ReadThread(socket, dataInputStream).start();
        } catch (SocketException e) {
            System.err.println("Got disconnected from the game");
        } catch (IOException e) {
            System.err.println("I/O error occurred");
        }
    }
}
