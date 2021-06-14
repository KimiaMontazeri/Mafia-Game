package mafia;

import mafia.model.chatroom.client.Client;

/**
 * Contains the main method for the client side of the program
 * @author KIMIA
 * @version 1.0
 */
public class ClientMain
{
    /**
     * Starts the client side of the program
     */
    public static void main(String[] args)
    {
        Client client = new Client();
        client.connectToServer();
    }
}
