package mafia.chatroom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * This class handles registering the clients in the game
 */
public class RegisterHandler extends Thread
{
    private final Server server;
    private final ServerSocket serverSocket;
    private int clientNum;
    public boolean isWaiting;

    public RegisterHandler(Server server, ServerSocket serverSocket) {
        this.server = server;
        this.serverSocket = serverSocket;
        clientNum = 0;
        isWaiting = true;
    }

    @Override
    public void run()
    {
        while (isWaiting)
        {
            try {
                Socket socket = serverSocket.accept();
                clientNum++;
                System.out.println("[Client " + clientNum + "] got connected.");

                ClientHandler newUser = new ClientHandler(socket, server, this);
                server.getUsers().put(newUser, false);
                server.getPool().execute(newUser);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean register(ClientHandler clientHandler)
    {
        if (server.getUsers().containsKey(clientHandler))
            return false;

        server.getUsers().put(clientHandler, false);
        return true;
    }

    public synchronized boolean addReadyClient(ClientHandler clientHandler)
    {
        HashMap<ClientHandler, Boolean> users = server.getUsers();
        if (users.containsKey(clientHandler))
        {
            users.put(clientHandler, true);
            return true;
        }
        return false;
    }

}
