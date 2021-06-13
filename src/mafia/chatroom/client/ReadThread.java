package mafia.chatroom.client;

import mafia.view.Display;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ReadThread extends Thread
{
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
                if (text.contains("HISTORY"))
                    Display.displayHistory(text);
                else if (text.equals("DISCONNECT"))
                    break;
                else
                    Display.print(text);
            }
            socket.close();
        } catch (SocketException e) {
            System.err.println("Got disconnected from the game");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("I/O error occurred");
            e.printStackTrace(); // TODO remove this at last
        }
    }
}
