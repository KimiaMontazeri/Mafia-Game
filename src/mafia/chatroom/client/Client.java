package mafia.chatroom.client;

import mafia.view.Display;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    private final int port;

    public Client() {
        port = getPortFromUser();
    }

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
        }
        catch (IOException e) {
            System.err.println("Got disconnected from the game");
        }
    }
}
