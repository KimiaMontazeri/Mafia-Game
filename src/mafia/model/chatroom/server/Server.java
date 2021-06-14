package mafia.model.chatroom.server;

import mafia.model.GameData;
import mafia.model.element.Message;
import mafia.model.element.Phase;
import mafia.model.element.Player;
import mafia.model.gamelogic.PlayerManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the main server of the game which controls all the clients and broadcasts messages to all clients
 * @author KIMIA
 * @version 1.0
 */
public class Server
{
    // server properties
    private final int port;
    private ServerSocket serverSocket;
    private ExecutorService pool;

    // user properties
    private final HashMap<ClientHandler, Boolean> users; // clientHandler -> isReady, isOnline

    // game properties
    private final GameData gameData;

    /**
     *
     * @param port sets the server's port to this parameter
     */
    public Server(int port)
    {
        this.port = port;
        users = new HashMap<>();
        gameData = GameData.getInstance();
    }

    /**
     *
     * @return thread pool of ClientHandlers
     */
    public ExecutorService getPool() {
        return pool;
    }

    /**
     * Executes the server
     * Creates a register handler and gives it 2 minutes to register clients to the game
     */
    public void execute()
    {
        pool = Executors.newCachedThreadPool();
        try // removed try with resource
        {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);
            RegisterHandler registerHandler = new RegisterHandler(this, serverSocket);
            registerHandler.start();
            Thread.sleep(120000); // 2 min
            registerHandler.isWaiting = false; // time is up, no more client can join the game
            pool.shutdown();
        }
        catch (IOException | InterruptedException e) {
            System.err.println("Disconnected from clients");
            e.printStackTrace();
        }
    }

    /**
     * Prepares the game with the players who are ready
     * @return a list of the ready player's usernames
     */
    public ArrayList<String> prepareGame()
    {
        ArrayList<String> usernames = new ArrayList<>();
        for (Map.Entry<ClientHandler, Boolean> entry : users.entrySet())
        {
            if (entry.getValue()) // is ready and online
                usernames.add(entry.getKey().getUsername());
        }
        return usernames;
    }

    /**
     * Broadcasts a message to all players (if the player is awake)
     * @param message message to broadcast
     */
    public synchronized void broadcast(Message message)
    {
        // check if the sender is alive and can speak or is the game's God
        String sender = message.getSender();
        if (canSendMessageFrom(sender) || gameData.getCurrentPhase() == Phase.NOT_STARTED)
        {
            if (gameData.electionIsOn() && !sender.equals("GOD"))
                collectVote(message); // no need to broadcast the votes to others
            else
            {
                // check if the receivers are awake (Asleep players won't receive the message)
                for (Map.Entry<ClientHandler, Boolean> entry : users.entrySet())
                {
                    // send the message to the awake players (also, check if the player is online)
                    if (canSendMessageTo(entry) || gameData.getCurrentPhase() == Phase.NOT_STARTED)
                        entry.getKey().sendMessage(message);
                }
            }
            // store the player's messages in the gameData
            if (!sender.equals("GOD"))
                gameData.addMessage(message);
            System.out.println(message);
        }
    }

    /**
     * Collects votes by adding them to the GameData's election
     * @param message a message that contains the name of a candidate
     */
    public void collectVote(Message message)
    {
        String candidate = message.getText();
        Message answer;
        if (usernameExists(candidate))
        {
            gameData.getLastElection().addVote(gameData.findPlayer(message.getSender()), gameData.findPlayer(candidate));
            answer = new Message("Your vote has been recorded in the game, " +
                    "but you can still change it until the voting time is over", "GOD");
        }
        else
        {
            answer = new Message("Your chosen candidate is invalid! " +
                    "You can try again until the voting time is over", "GOD");
        }
        findClientHandler(message.getSender()).sendMessage(answer);
    }

    /**
     * Checks if it can send a message to the client
     * @param entry entry of the hashmap of clients
     * @return true if it can send message to the client
     */
    private boolean canSendMessageTo(Map.Entry<ClientHandler, Boolean> entry) {
        return gameData.isAwake(entry.getKey().getUsername()) && entry.getValue();
    }

    /**
     * Checks if it can send message from the client
     * @param sender sender of the message
     * @return true if it can send message from the client
     */
    private boolean canSendMessageFrom(String sender)
    {
        return sender.equals("GOD") || (gameData.canSpeak(sender)
                                    &&  gameData.isAwake(sender));
    }

    /**
     * Checks if the given username exits in the game or not
     * @param username username to find
     * @return true if the username exits
     */
    public synchronized boolean usernameExists(String username) {
        return findClientHandler(username) != null;
    }

    /**
     * Finds the client handler with the given username
     * @param username client's username
     * @return the corresponding client handler
     */
    public ClientHandler findClientHandler(String username)
    {
        for (ClientHandler clientHandler : users.keySet())
        {
            if (clientHandler.getUsername().equals(username))
                return clientHandler;
        }
        return null;
    }

    /**
     * Adds a client to the list of clients but assumes that they are not ready to play yet
     * @param clientHandler client to add
     */
    public synchronized void addClient(ClientHandler clientHandler) {
        users.put(clientHandler, false);
    }

    /**
     * Removes a client handler from the list of client handlers and the game's players
     * @param clientHandler client handler to remove
     */
    public synchronized void removeClient(ClientHandler clientHandler)
    {
        users.remove(clientHandler);
        Player removedPlayer = gameData.findPlayer(clientHandler.getUsername());
        if (removedPlayer != null)
        {
            PlayerManager.handlePlayerRemoval(removedPlayer);
            gameData.getDeadPlayers().add(removedPlayer);
            gameData.getAlivePlayers().remove(removedPlayer);
        }
    }

    /**
     *
     * @param clientHandler a client that has declared that they are ready to play
     */
    public synchronized void addReadyClient(ClientHandler clientHandler)
    {
        if (users.containsKey(clientHandler))
            users.put(clientHandler, true);
    }

    /**
     * Disconnects a client that has the given username from the server
     * @param username username of the client to disconnect
     */
    public void shutDownClient(String username) // this method is called form GameManager
    {
        ClientHandler clientHandler = findClientHandler(username);
        if (clientHandler != null)
        {
            clientHandler.sendMessage(new Message("DISCONNECT", "GOD"));
            removeClient(clientHandler);
        }
    }

    /**
     * Shuts down the whole server and the game
     */
    public void shutDownServer()
    {
        try {
            for (ClientHandler clientHandler : users.keySet())
                clientHandler.setRunning(false);
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Server socket is closed");
        }
    }
}
