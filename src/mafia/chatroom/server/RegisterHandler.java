package mafia.chatroom.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

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
                server.getPool().execute(newUser);
            }
            catch (SocketException e) {
                System.out.println("Game has ended, server is closed");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean register(ClientHandler clientHandler)
    {
        if (server.usernameExists(clientHandler.getUsername()))
            return false;

        server.addClient(clientHandler);
        return true;
    }

    public synchronized void addReadyClient(ClientHandler clientHandler) {
        server.addReadyClient(clientHandler);
    }

}
