package mafia.model.chatroom.server;

import mafia.model.element.Message;
import mafia.model.utils.Cache;

import java.io.*;
import java.net.Socket;

/**
 * This class handles one client that has got connected to the game
 */
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

    /**
     *
     * @param socket the socket that client is connected to
     * @param server game's major server
     * @param registerHandler a handler which manages client registers
     */
    public ClientHandler(Socket socket, Server server, RegisterHandler registerHandler)
    {
        this.socket = socket;
        this.server = server;
        this.registerHandler = registerHandler;
        isRunning = true;
        isRegistered = false;
        isReady = false;
    }

    /**
     *
     * @param running whether the thread has to keep running or not
     */
    public void setRunning(boolean running) {
        isRunning = running;
    }

    /**
     *
     * @return client's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Registers the client to the game
     * Does not allow repetitive usernames
     * creates a cache to store the client's messages at last
     * @throws IOException if an I/O error occurs while sending/receiving messages from the client
     */
    public void register() throws IOException
    {
        if (!isRegistered)
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
            dataOutputStream.writeUTF("Successfully registered!\n");
            System.out.println("[" + username + "] got registered");
            isRegistered = true;
            msgHistory = new Cache(username); // creates a new file with the client's name
        }
    }

    /**
     * Starts the thread , creates data inout/output streams and waits for the client to send a message
     * Handles the client's messages afterwards and saves their message into a file
     */
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
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("client " + username + " got disconnected");
        } finally {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                System.err.println("Could not close output stream");
            } finally {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    System.err.println("Could not close input stream");
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.err.println("Could not close socket");
                    }
                }
            }
        }
    }

    /**
     * Sends a specific message to the client
     * @param msg message to send
     */
    public void sendMessage(Message msg)
    {
        try {
            if (msg.getText().equals("DISCONNECT"))
                terminate();
            // send the message to the client
            dataOutputStream.writeUTF(msg.toString());
            // write the server's messages to file
            msgHistory.addMessage(msg.toString());

        } catch (IOException e) {
            System.out.println("client " + username + " got disconnected");
        }
    }

    /**
     * Handles the client's commands
     * @param command command to handle
     * @throws IOException if an I/O error occurs while sending/receiving messages from the client
     */
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

    /**
     * Terminates the client handler by notifying the server
     */
    public void terminate()
    {
        server.broadcast(new Message("Left the game", username));
        server.removeClient(this);
        isRunning = false;
    }
}
