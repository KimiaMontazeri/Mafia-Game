package mafia.chatroom.client;


import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class WriteThread extends Thread
{
    private final DataOutputStream dataOutputStream;
    private final Socket socket;

    public WriteThread(Socket socket, DataOutputStream dataOutputStream)
    {
        this.dataOutputStream = dataOutputStream;
        this.socket = socket;
    }

    @Override
    public void run()
    {
        Scanner scanner = new Scanner(System.in);
        try
        {
            String text;

            do
            {
                text = scanner.nextLine();
                dataOutputStream.writeUTF(text);

            } while (!text.equals("exit")); // user won't be able to send messages if they exit the game
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
