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
    private boolean isRunning;

    // streams
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    // client's data
    private String username;
    private boolean isRegistered, isReady;
    private Cache msgHistory;

    public ClientHandler(Socket socket, Server server, RegisterHandler registerHandler)
    {
        this.socket = socket;
        this.server = server;
        this.registerHandler = registerHandler;
        isRunning = true;
        isRegistered = false;
        isReady = false;
    }

    public void setRunning(boolean running) {
        isRunning = running;
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
            do {
                clientMsg = dataInputStream.readUTF();
                handleCommands(clientMsg);
                if (isRegistered)
                    msgHistory.addMessage(clientMsg);  // writes the client's messages to file
            } while (isRunning); // client has exited the game
            dataOutputStream.writeUTF("DISCONNECT"); // notify the client that they got disconnected
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("client " + username + " got disconnected");
        }
    }

    public void sendMessage(Message msg)
    {
        try {
            if (msg.getText().equals("DISCONNECT"))
                terminate();
            // send the message to the client
            dataOutputStream.writeUTF(msg.toString());
            msgHistory.addMessage(msg.toString()); // writes the server's messages to file

        } catch (IOException e) {
            System.out.println("client " + username + " got disconnected");
        }
    }

    public void handleCommands(String command) throws IOException
    {
        switch (command.toUpperCase())
        {
            case "REGISTER" -> register();
            case "READY" -> {
                if (isRegistered && !isReady) {
                    registerHandler.addReadyClient(this);
                    dataOutputStream.writeUTF("""
                            You are added to the game's members!
                            Waiting for others to join, the game will start in just a moment...
                            """);
                    isReady = true;
                    System.out.println("[" + username + "] is ready to play");
                } else if (isRegistered)
                    dataOutputStream.writeUTF("You have been added to the game's members before!");
                else
                    dataOutputStream.writeUTF("Register first!");
            }
            case "HISTORY" -> dataOutputStream.writeUTF("HISTORY\n" + msgHistory.getHistory());
            case "HELP" ->
                    dataOutputStream.writeUTF("""
                        Game's commands are:
                        REGISTER -> Register in the game
                        READY -> Declare that you're all set to start the game
                        HISTORY -> Get the chat history
                        EXIT -> Exit the game""");

            case "EXIT", "DISCONNECT" -> terminate();
            default -> {
                if (isRegistered)
                    server.broadcast(new Message(command, username));
            }
        }
    }

    public void terminate()
    {
        server.broadcast(new Message("Left the game", username));
        server.removeClient(this); // method not working :|
        isRunning = false;
    }
}
