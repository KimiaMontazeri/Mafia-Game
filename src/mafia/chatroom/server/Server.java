package mafia.chatroom.server;

import mafia.model.GameData;
import mafia.model.element.Message;
import mafia.model.element.Phase;
import mafia.model.element.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public Server(int port)
    {
        this.port = port;
        users = new HashMap<>();
        gameData = GameData.getInstance();
    }

    public HashMap<ClientHandler, Boolean> getUsers() {
        return users;
    }

    public ExecutorService getPool() {
        return pool;
    }

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

    public ArrayList<String> prepareGame()
    {
        ArrayList<String> usernames = new ArrayList<>();
        for (Map.Entry<ClientHandler, Boolean> entry : users.entrySet())
        {
            if (entry.getValue()) // is ready and online
                usernames.add(entry.getKey().getUsername());
            else users.remove(entry.getKey()); // TODO not sure
        }
        return usernames;
    }

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

    private boolean canSendMessageTo(Map.Entry<ClientHandler, Boolean> entry) {
        return gameData.isAwake(entry.getKey().getUsername()) && entry.getValue();
    }

    private boolean canSendMessageFrom(String sender)
    {
        return sender.equals("GOD") || (gameData.canSpeak(sender)
                                    &&  gameData.isAwake(sender));
    }

    public synchronized boolean usernameExists(String username) {
        return findClientHandler(username) != null;
    }

    public ClientHandler findClientHandler(String username)
    {
        for (ClientHandler clientHandler : users.keySet())
        {
            if (clientHandler.getUsername().equals(username))
                return clientHandler;
        }
        return null;
    }

    public synchronized void addClient(ClientHandler clientHandler) {
        users.put(clientHandler, false);
    }

    public synchronized void removeClient(ClientHandler clientHandler)
    {
        users.remove(clientHandler);
        Player removedPlayer = gameData.findPlayer(clientHandler.getUsername());
        if (removedPlayer != null)
        {
            gameData.handlePlayerRemoval(removedPlayer);
            gameData.getDeadPlayers().add(removedPlayer);
            gameData.getAlivePlayers().remove(removedPlayer);
        }
    }

    public synchronized void addReadyClient(ClientHandler clientHandler)
    {
        if (users.containsKey(clientHandler))
            users.put(clientHandler, true);
    }

    // this method is called form GameManager
    public void shutDownClient(String username)
    {
        ClientHandler clientHandler = findClientHandler(username);
        clientHandler.sendMessage(new Message("DISCONNECT", "GOD"));
        removeClient(clientHandler);
    }

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
