package mafia.chatroom.client;

import mafia.model.element.Message;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class WriteThread extends Thread
{
    private final DataOutputStream dataOutputStream;

    public WriteThread(Socket socket, DataOutputStream dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
    }

//    public void openStreams()
//    {
//        try
//        {
//            OutputStream out = socket.getOutputStream();
//            objectOutputStream = new ObjectOutputStream(out);
//        }
//        catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }

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
//                Message msg = new Message(text, username);
//                objectOutputStream.writeObject(msg);

            } while (!text.equals("exit")); // user won't be able to send messages if they exit the game
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
