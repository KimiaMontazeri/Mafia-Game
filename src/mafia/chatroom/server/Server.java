package mafia.chatroom.server;

import mafia.model.GameData;
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
    private HashMap<ClientHandler, Boolean> users; // clientHandler -> isReady, isOnline

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

    // game manager firstly will call this method
    public void execute()
    {
        pool = Executors.newCachedThreadPool();
        try
        {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);
            RegisterHandler registerHandler = new RegisterHandler(this, serverSocket);
            registerHandler.start();
            Thread.sleep(70000);
            registerHandler.isWaiting = false; // time is up, no more client can join the game
            broadcast(new Message("Time is up everyone!", "GOD"));
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // game manager will call this method
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

    public synchronized void broadcast(Message message)
    {
        // check if the sender is alive and can speak or is the game's God
        String sender = message.getSender();
        if (sender.equals("GOD") || (gameData.canSpeak(sender) && gameData.isAlive(sender)))
        {
            // check if the receivers are awake (Asleep players won't receive the message)
            for (Map.Entry<ClientHandler, Boolean> entry : users.entrySet())
            {
                // send the message to the awake players (also, check if the player is online)
                if (!gameData.isAsleep(entry.getKey().getUsername()) && entry.getValue())
                    entry.getKey().sendMessage(message);
            }
        }
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

    public synchronized void removeClient(ClientHandler clientHandler)
    {
        // remove the corresponding player from gameData
        users.remove(clientHandler);
        // terminate the client thread (the method called below is not complete yet)
        clientHandler.terminate();
    }

    // server will be running on its own thread, when all the players have registered and the game starts
//    @Override
//    public void run()
//    {
//        // use the server socket
//    }

    // TODO add methods to check if a client has got disconnected and handle it

}
