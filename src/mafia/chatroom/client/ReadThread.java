package mafia.chatroom.client;

import mafia.view.Display;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ReadThread extends Thread
{
    private ObjectInputStream objectInputStream;

    private final DataInputStream dataInputStream;
    private final Socket socket;

    public ReadThread(Socket socket, DataInputStream dataInputStream)
    {
        this.socket = socket;
        this.dataInputStream = dataInputStream;
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                String text = dataInputStream.readUTF();
                Display.print(text);
                // break from the loop if the game is ended
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
