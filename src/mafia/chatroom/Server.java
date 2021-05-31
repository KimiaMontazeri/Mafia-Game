package mafia.chatroom;

import mafia.model.GameData;
import mafia.model.element.Message;
import mafia.model.gamelogic.GameSetup;
import mafia.view.Display;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server
{
    private final int port;
    private final Set<ClientHandler> userThreads;
//    private final Set<String> usernames = new HashSet<>();

    // the game's information
    private final GameData gameData;   // current state of the game
    private final GameSetup gameSetup; // used for setting up the game

    public Server(int port)
    {
        this.port = port;
        userThreads = new HashSet<>();
        gameData = GameData.getInstance();
        gameSetup = new GameSetup();
    }

    public void execute()
    {
        try (ServerSocket serverSocket = new ServerSocket(port))
        {

            Display.print("Chat Server is listening on port " + port);

            while (true)
            {
                Socket socket = serverSocket.accept();
                Display.print("New user connected");

                ClientHandler newUser = new ClientHandler(socket, this);
                userThreads.add(newUser);
                Thread t = new Thread(newUser);
                t.start();

                // TODO if (game is about to start) break -> the condition is checked by a game logic element

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method checks if a username exists in the database or not
     * @param username username of a newly connected client
     * @return true if the client's username is valid
     */
    public synchronized boolean register(String username)
    {
        // TODO complete this simple method (don't forget to add the player to the GameData)
        return false;
    }

    // this method, may be synchronized
    public void broadcast(Message msg)
    {
        String sender = msg.getSender();
        for (ClientHandler client : userThreads)
        {
            // TODO check if the message should be displayed to a certain player or not (iterate all the players in the database)
            // the above condition in also checked on the client side but here is more important i guess
            // check if the client is awake or not (compare player's username with the clientHandler's username)
            client.sendMessage(msg);
        }
    }

    public void welcomeAllPlayers()
    {
        // TODO complete this simple method (use all the READY players stored in the database)
        String text = "List of the players:\n";
        Message msg = new Message(text, "GOD");
    }
}
