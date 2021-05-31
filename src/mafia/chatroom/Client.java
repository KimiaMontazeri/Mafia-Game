package mafia.chatroom;

import mafia.model.element.Player;
import mafia.view.Display;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    private Player player;
    private Socket socket;
    private int port;
    private InputStream in;
    private OutputStream out;

    public Client()
    {
        port = getPortFromUser();
        player = new Player(getUsernameFromUser());
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

    private String getUsernameFromUser()
    {
        Scanner scanner = new Scanner(System.in);
        while (true)
        {
            Display.print("Enter your name: ");
            String username = scanner.nextLine();
            // TODO check if the username already exists, if everything is fine, return the username
            return username;
        }
    }

    public void connectToServer()
    {
        try
        {
            socket = new Socket("127.0.0.1", port);
            Display.print("Connected to the game!\n");

            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Player getPlayer() {
        return player;
    }
}
