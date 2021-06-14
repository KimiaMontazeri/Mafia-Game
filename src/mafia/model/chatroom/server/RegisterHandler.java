package mafia.model.chatroom.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.RejectedExecutionException;

/**
 * This class handles registering the clients in the game
 * @author KIMIA
 * @version 1.0
 */
public class RegisterHandler extends Thread
{
    private final Server server;
    private final ServerSocket serverSocket;
    private int clientNum;
    public boolean isWaiting;

    /**
     *
     * @param server central server of the game
     * @param serverSocket server's welcoming socket
     */
    public RegisterHandler(Server server, ServerSocket serverSocket) {
        this.server = server;
        this.serverSocket = serverSocket;
        clientNum = 0;
        isWaiting = true;
    }

    /**
     * Starts the thread by waiting for clients to join and adds a client handler to server's the executor service
     * Waits for clients until the server tells it to stop
     */
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
            catch (RejectedExecutionException e) {
                System.err.println("Cannot accept client anymore");
            }
            catch (SocketException e) {
                System.err.println("Game has ended, server is closed");
            }
            catch (IOException e) {
                System.err.println("I/O error occurred");
            }
        }
    }

    /**
     *
     * @param clientHandler a client to add to the game's registered clients
     * @return false if the client handler's username already exits
     */
    public synchronized boolean register(ClientHandler clientHandler)
    {
        if (server.usernameExists(clientHandler.getUsername()))
            return false;

        server.addClient(clientHandler);
        return true;
    }

    /**
     *
     * @param clientHandler a client to add to the game's ready clients
     */
    public synchronized void addReadyClient(ClientHandler clientHandler) {
        server.addReadyClient(clientHandler);
    }

}
