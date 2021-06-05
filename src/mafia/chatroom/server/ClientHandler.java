package mafia.chatroom.server;

import mafia.model.element.Message;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    // sockets and server
    private final Socket socket;
    private final Server server;
    private final RegisterHandler registerHandler;

    // streams
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

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
            dataOutputStream.writeUTF("Enter your name: ");
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
            System.out.println("[" + username + "] got registered");
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
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            register();
            if (isReady())
            {
                String clientMsg;
                do {
                    clientMsg = dataInputStream.readUTF();
                    server.broadcast(new Message(clientMsg, username));
                } while (!clientMsg.equals("exit"));
//                objectOutputStream = new ObjectOutputStream(out);
//                objectInputStream = new ObjectInputStream(in);
//
//                Message clientMessage;
//                do
//                {
//                    clientMessage = (Message) objectInputStream.readObject();
//                    server.broadcast(clientMessage);
//                } while (!clientMessage.getText().equals("exit"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.removeClient(this);
    }

    public void sendMessage(Message msg)
    {
        try {
            dataOutputStream.writeUTF(msg.toString());
//            objectOutputStream.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Message convertToMessage(String text)
    {
        StringBuilder sender = new StringBuilder();
        int lastIndexOfSender = text.indexOf(':');
        String content = text.substring(lastIndexOfSender + 1);
        char[] chars = text.toCharArray();

        for (int i = 0; i <= lastIndexOfSender; i++)
        {
            if (chars[i] != '[' && chars[i] != ']')
                sender.append(chars[i]);
        }
        return new Message(sender.toString(), content);
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
