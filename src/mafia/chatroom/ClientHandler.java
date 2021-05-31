package mafia.chatroom;

import mafia.model.element.Message;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    private final Socket socket;
    private final Server server;
    private InputStream in;
    private OutputStream out;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String username;

    public ClientHandler(Socket socket, Server server)
    {
        this.socket = socket;
        this.server = server;
    }

    public void openStreams()
    {
        try
        {
            in = socket.getInputStream();
            out = socket.getOutputStream();

            objectInputStream = new ObjectInputStream(in);
            objectOutputStream = new ObjectOutputStream(out);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register() throws IOException
    {
        if (this.username == null)
        {
            DataInputStream dataInputStream = new DataInputStream(in);
            DataOutputStream dataOutputStream = new DataOutputStream(out);

            String username = dataInputStream.readUTF();

            while (!server.register(username))
            {
                dataOutputStream.writeUTF("This username already exists :(\nTry again please: ");
                username = dataInputStream.readUTF();
            }
            // notify the client that everything is fine
            this.username = username;
            dataOutputStream.writeUTF("Successfully registered!");

            dataInputStream.close();
            dataOutputStream.close();
        }
    }

    public void isReady() throws IOException
    {
        DataInputStream dataInputStream = new DataInputStream(in);
        DataOutputStream dataOutputStream = new DataOutputStream(out);

        String answer = "";
        do
        {
            dataOutputStream.writeUTF("Type 'ready' if you are...");
            answer = dataInputStream.readUTF().toLowerCase();
        }while (!answer.equals("ready"));

        dataInputStream.close();
        dataOutputStream.close();
    }

    @Override
    public void run()
    {
        try
        {
            register();
            isReady();
            Message clientMessage;
            do
            {
                clientMessage = (Message) objectInputStream.readObject();
                server.broadcast(clientMessage);
            }while (clientMessage.getText().equals("exit"));
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message msg)
    {
        try {
            objectOutputStream.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * this method is called from the server when the client hasn't said
     * that they are ready but the game is starting
     */
    public void terminate()
    {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
