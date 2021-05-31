package mafia.chatroom;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    private Socket socket;
    private Server server;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public ClientHandler(Socket socket, Server server)
    {
        this.socket = socket;
        this.server = server;
    }

    public void openStreams()
    {
        try
        {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            objectInputStream = new ObjectInputStream(in);
            objectOutputStream = new ObjectOutputStream(out);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run()
    {
        
    }
}
