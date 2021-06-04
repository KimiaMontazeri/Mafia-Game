package mafia;

import mafia.chatroom.client.Client;

public class ClientMain
{
    public static void main(String[] args)
    {
        Client client = new Client();
        client.connectToServer();
    }
}
