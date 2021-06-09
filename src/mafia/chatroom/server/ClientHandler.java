package mafia.chatroom.server;

import mafia.model.element.Message;
import mafia.model.utils.Cache;

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
    private boolean isRegistered;
    private Cache msgHistory;

    public ClientHandler(Socket socket, Server server, RegisterHandler registerHandler)
    {
        this.socket = socket;
        this.server = server;
        this.registerHandler = registerHandler;
        isRegistered = false;
    }

    public String getUsername() {
        return username;
    }

    public void register() throws IOException
    {
        if (!isRegistered) // or this.username == null
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
            isRegistered = true;
            msgHistory = new Cache(username); // creates a new file with the client's name
        }
    }

    @Override
    public void run()
    {
        try
        {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String clientMsg;
            boolean result;
            do {
                clientMsg = dataInputStream.readUTF();
                result = handleCommands(clientMsg);
                if (isRegistered)
                    msgHistory.addMessage(clientMsg);  // writes the client's messages to file
            } while (result); // client has exited the game

        } catch (IOException e) {
            e.printStackTrace();
        }
        server.getUsers().remove(this);
    }

    public void sendMessage(Message msg)
    {
        try {
            dataOutputStream.writeUTF(msg.toString());
            msgHistory.addMessage(msg.toString()); // writes the server's messages to file
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

    private boolean handleCommands(String command) throws IOException
    {
        switch (command.toUpperCase())
        {
            case "REGISTER" -> {
                register();
                return true;
            }
            case "READY" -> {
                if (isRegistered) {
                    registerHandler.addReadyClient(this);
                    dataOutputStream.writeUTF("You are added to the game's members!\n" +
                            "Waiting for others to join, the game will start in just a moment...");
                    System.out.println("[" + username + "] is ready to play");
                } else dataOutputStream.writeUTF("Register first!");
                return true;
            }
            case "HISTORY" -> {
                dataOutputStream.writeUTF("HISTORY\n" + msgHistory.getHistory());
                return true;
            }
            case "HELP" -> {
                dataOutputStream.writeUTF("""
                        Game's commands are:
                        REGISTER -> Register in the game
                        READY -> Declare that you're all set to start the game
                        HISTORY -> for getting the chat history
                        EXIT -> for exiting the game""");
                return true;
            }
            case "EXIT", "DISCONNECT" -> {
                dataOutputStream.writeUTF("You left the game :( See you next time!");
                server.broadcast(new Message("Left the game", username));
                server.getUsers().remove(this);
                return false;
            }
            default -> {
                if (isRegistered)
                    server.broadcast(new Message(command, username));
                return true;
            }
        }
    }

    public void terminate()
    {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
