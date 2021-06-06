package mafia.chatroom.client;

import mafia.view.Display;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    private final int port;

    // streams
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    // client's data
    private String username;

    public Client() {
        port = getPortFromUser();
    }

    public String getUsername() {
        return username;
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

    private String register() throws IOException
    {
        Scanner scanner = new Scanner(System.in);

        Display.print(dataInputStream.readUTF());
        String username;
        String serverResponse;

        do
        {
            username = scanner.nextLine();
            dataOutputStream.writeUTF(username);
            serverResponse = dataInputStream.readUTF();
            Display.print(serverResponse);
        }while (!serverResponse.equalsIgnoreCase("Successfully registered!\n"));

        return username;
    }

    public void readyToPlay() throws IOException
    {
        Scanner scanner = new Scanner(System.in);

        String answer = "";
        Display.print(dataInputStream.readUTF());

        // wait for the client to get ready
        while (!answer.equals("ready")) {
            answer = scanner.nextLine().toLowerCase();
        }
        // notify the server that the client is ready to play
        dataOutputStream.writeUTF(answer);
        // display the server's answer
        Display.print(dataInputStream.readUTF());
    }

    public void connectToServer()
    {
        try
        {
            // server's data
            Socket socket = new Socket("127.0.0.1", port);
            Display.print("Connected to the game!\n");

            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            // register
            username = register();

            // the client announces that they are ready
            readyToPlay();

            // directed to the chatroom and the game will start
            new WriteThread(socket, dataOutputStream).start();
            new ReadThread(socket, dataInputStream).start();
        }
        catch (IOException e) {
            System.err.println("I/O ERROR OCCURRED");
        }
    }
}
