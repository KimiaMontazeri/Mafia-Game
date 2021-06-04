package mafia.chatroom.server;

import mafia.model.GameData;
import mafia.model.element.Election;
import mafia.model.element.Message;

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
    private boolean electionIsOn = false;

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

    public boolean electionIsOn() {
        return electionIsOn;
    }

    public void execute()
    {
        pool = Executors.newCachedThreadPool();
        try
        {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);
            RegisterHandler registerHandler = new RegisterHandler(this, serverSocket);
            registerHandler.start();
            Thread.sleep(60000); // 2 min
            registerHandler.isWaiting = false; // time is up, no more client can join the game
            System.out.println("Server is preparing the game, no more clients can join");
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startElection()
    {
        // god has told players that it's time to vote
        electionIsOn = true;
        gameData.setLastElection(new Election());
        try {
            Thread.sleep(20000);
            broadcast(new Message("10 seconds left from the election!", "GOD"));
            Thread.sleep(10000);
            broadcast(new Message("Election time has ended!", "GOD"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            electionIsOn = false;
        }
    }

    public ArrayList<String> prepareGame()
    {
        ArrayList<String> usernames = new ArrayList<>();
        for (Map.Entry<ClientHandler, Boolean> entry : users.entrySet())
        {
            if (entry.getValue()) // is ready and online
                usernames.add(entry.getKey().getUsername());
            // else remove the offline or "not-ready" player form the hashmap-> not sure about this
        }
        return usernames;
    }

    // god calls this method when he wants to ask a yes no question from player(s)
    public void yesNoQuestion(Message message)
    {

    }

    public synchronized void broadcast(Message message)
    {
        // check if the sender is alive and can speak or is the game's God
        String sender = message.getSender();
        if (canSendMessageFrom(sender))
        {
            if (electionIsOn) collectVote(message);
            // check if the receivers are awake (Asleep players won't receive the message)
            for (Map.Entry<ClientHandler, Boolean> entry : users.entrySet())
            {
                // send the message to the awake players (also, check if the player is online)
                if (canSendMessageTo(entry, sender))
                    entry.getKey().sendMessage(message);
            }
            // store the message in the gameData
            gameData.addMessage(message);
        }
    }

    public void collectVote(Message message)
    {
        String candidate = message.getText();
        if (usernameExists(candidate))
            gameData.getLastElection().addVote(message.getSender(), candidate);
        else
        {
            Message errorMsg = new Message("Your chosen candidate is invalid! Try again: ", "GOD");
            findClientHandler(message.getSender()).sendMessage(errorMsg);
        }
    }

    private boolean canSendMessageTo(Map.Entry<ClientHandler, Boolean> entry, String sender)
    {
        return !gameData.isAsleep(entry.getKey().getUsername()) &&
                entry.getValue() &&
                !entry.getKey().getUsername().equals(sender);
    }

    private boolean canSendMessageFrom(String sender)
    {
        return sender.equals("GOD") || (gameData.canSpeak(sender)
                                    && !gameData.isAsleep(sender)
                                    && gameData.isAlive(sender));
    }

    public synchronized boolean usernameExists(String username)
    {
        for (ClientHandler clientHandler : users.keySet())
        {
            if (clientHandler.getUsername().equals(username))
                return true;
        }
        return false;
    }

    // TODO make this method and its above method into one single method with return ClientHandler
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

    public synchronized boolean addReadyClient(ClientHandler clientHandler)
    {
        if (users.containsKey(clientHandler))
        {
            users.put(clientHandler, true);
            return true;
        }
        return false;
    }

    public synchronized void removeClient(ClientHandler clientHandler)
    {
        // remove the corresponding player from gameData
        users.remove(clientHandler);
        // terminate the client thread (the method called below is not complete yet)
        clientHandler.terminate();
    }

    // TODO add methods to check if a client has got disconnected and handle it

}
