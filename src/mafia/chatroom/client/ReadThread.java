package mafia.chatroom.client;

import mafia.model.element.Message;
import mafia.view.Display;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ReadThread extends Thread
{
    private ObjectInputStream objectInputStream;

    private DataInputStream dataInputStream;
    private final Socket socket;
    private final String username;

    public ReadThread(Socket socket, String username, DataInputStream dataInputStream)
    {
        this.socket = socket;
        this.username = username;
        this.dataInputStream = dataInputStream;
    }

//    public void openStreams() // TODO add throws IOException to the method's signature
//    {
//        try
//        {
//            InputStream input = socket.getInputStream();
//            objectInputStream = new ObjectInputStream(input);
//        }
//        catch (IOException ex) {
//            System.err.println("COULD NOT OPEN I/O STREAMS");
//        }
//    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                String text = dataInputStream.readUTF();
                Display.print(text);

//                Message receivedMsg = (Message) objectInputStream.readObject();
//                // show the message to the client, if they are one of the receivers
//                Display.print(receivedMsg);

                // if statement below may be modified later
                // prints the username after displaying the server's message
                // indicates that an answer is expected from the client
                if (username != null) {
                    Display.print("[" + username + "]: ");
                }

                // break from the loop if the game is ended
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
