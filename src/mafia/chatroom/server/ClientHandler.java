package mafia.chatroom.server;

import mafia.model.element.Message;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable
{
    // sockets and server
    private final Socket socket;
    private final Server server;
    private final RegisterHandler registerHandler;

    // streams
    private InputStream in;
    private OutputStream out;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    // client's data
    private String username;

    public ClientHandler(Socket socket, Server server, RegisterHandler registerHandler)
    {
        this.socket = socket;
        this.server = server;
        this.registerHandler = registerHandler;
    }

    public String getUsername() {
        return username;
    }

    public void register() throws IOException
    {
        if (this.username == null)
        {
            String username = dataInputStream.readUTF();
            this.username = username;

            while (!registerHandler.register(this))
            {
                dataOutputStream.writeUTF("This username is taken :( Try another one: ");
                username = dataInputStream.readUTF();
                this.username = username;
            }
            // notify the client that everything is fine
            dataOutputStream.writeUTF("Successfully registered!\n");
            System.out.println("Client with username [" + username + "] got registered");
        }
    }

    public boolean isReady() throws IOException
    {
        String answer = "";

        dataOutputStream.writeUTF("Type 'ready' if you want to play\n");
        answer = dataInputStream.readUTF();
        if (answer.equalsIgnoreCase("ready"))
        {
            if (!registerHandler.addReadyClient(this)) {
                dataOutputStream.writeUTF("You are not registered in the game!");
                return false;
            }
            dataOutputStream.writeUTF("""
                    You are added to the game's members!
                    Waiting for others to join, the game will start in just a moment...
                    Don't go anywhere!!!
                    """);
            System.out.println("[" + username + "] is ready to play");
            return true;
        }
        return false;
    }

    @Override
    public void run()
    {
        try
        {
            in = socket.getInputStream();
            out = socket.getOutputStream();

            dataInputStream = new DataInputStream(in);
            dataOutputStream = new DataOutputStream(out);

            register();
            if (isReady())
            {
                objectInputStream = new ObjectInputStream(in);
                objectOutputStream = new ObjectOutputStream(out);

                Message clientMessage;
                do
                {
                    clientMessage = (Message) objectInputStream.readObject();
                    server.broadcast(clientMessage);
                } while (clientMessage.getText().equals("exit"));
            }
        }
        catch (SocketException e) {
            System.err.println("DISCONNECTED FROM CLIENT [" + username + "]");
        }
        catch (ClassNotFoundException e) {
            System.err.println("ERROR OCCURRED IN CONVERTING OBJECT TO MESSAGE");
        }
        catch (IOException e) {
            System.err.println("I/O ERROR OCCURRED");
        }
        server.removeClient(this);
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
