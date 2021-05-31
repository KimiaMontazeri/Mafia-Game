package mafia.chatroom;

import mafia.model.element.Player;
import mafia.view.Display;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    private Player player;
    private Socket socket;
    private final int port;

    public Client()
    {
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

    private String getUsernameFromUser() throws IOException
    {
        Scanner scanner = new Scanner(System.in);
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        Display.print("Enter your name: ");
        String username;
        String serverResponse;

        do
        {
            username = scanner.nextLine();
            dataOutputStream.writeUTF(username);
            serverResponse = dataInputStream.readUTF();
            Display.print(serverResponse);
        }while (!serverResponse.equals("Successfully registered!"));


        return username;
    }

    public void readyToPlay() throws IOException
    {
        Scanner scanner = new Scanner(System.in);
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        String answer = "";
        String command = dataInputStream.readUTF();
        Display.print(command);

        // wait for the client to get ready
        while (!answer.equals("ready"))
        {
            answer = scanner.nextLine().toLowerCase();
        }
        // notify the server that the client is ready to play
        dataOutputStream.writeUTF(answer);
    }

    public void connectToServer()
    {
        try
        {
            socket = new Socket("127.0.0.1", port);
            Display.print("Connected to the game!\n");

            // register
            player = new Player(getUsernameFromUser());

            // the client announces that they are ready
            readyToPlay();

            // directed to the chatroom
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
