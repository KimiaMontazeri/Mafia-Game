package mafia.chatroom.server;

import mafia.model.element.Message;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    // sockets and server
    private final Socket socket;
    private final Server server;
    private final RegisterHandler registerHandler;
    private boolean isRegistered;

    // streams
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    // client's data
    private String username;

    public ClientHandler(Socket socket, Server server, RegisterHandler registerHandler)
    {
        this.socket = socket;
        this.server = server;
        this.registerHandler = registerHandler;
        isRegistered = false;
    }

    public String getUsername() {
        return username;
    }

    public void register() throws IOException
    {
        if (this.username == null)
        {
            dataOutputStream.writeUTF("Enter your name: ");
            String username = dataInputStream.readUTF();
            this.username = username;

            while (!registerHandler.register(this))
            {
                dataOutputStream.writeUTF("This username is taken :( Try another one: ");
                username = dataInputStream.readUTF();
                this.username = username;
            }
            // notify the client that everything is fine
            dataOutputStream.writeUTF("Successfully registered!\n");
            System.out.println("[" + username + "] got registered");
            isRegistered = true;
        }
    }

//    public void ready() throws IOException
//    {
//        String answer = "";
//
//        dataOutputStream.writeUTF("Type 'ready' if you want to play\n");
//        answer = dataInputStream.readUTF();
//        if (answer.equalsIgnoreCase("ready"))
//        {
//            if (!registerHandler.addReadyClient(this)) {
//                dataOutputStream.writeUTF("You are not registered in the game!");
//            }
//            else
//            {
//                dataOutputStream.writeUTF("""
//                    You are added to the game's members!
//                    Waiting for others to join, the game will start in just a moment...
//                    """);
//                System.out.println("[" + username + "] is ready to play");
//            }
//        }
//    }

    @Override
    public void run()
    {
        try
        {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String clientMsg;
            boolean result;
            do {
                clientMsg = dataInputStream.readUTF();
                result = handleCommands(clientMsg);
            } while (result); // client has exited the game

//            register();
//            if (isReady())
//            {
//                String clientMsg;
//                do {
//                    clientMsg = dataInputStream.readUTF();
//                    if (clientMsg.equals("REVIEW")){
//                        server.getGameData().loadMessages();
//                    }
//                    server.broadcast(new Message(clientMsg, username));
//                } while (!clientMsg.equals("exit"));
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.getUsers().remove(this);
    }

    public void sendMessage(Message msg)
    {
        try {
            dataOutputStream.writeUTF(msg.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Message convertToMessage(String text)
    {
        StringBuilder sender = new StringBuilder();
        int lastIndexOfSender = text.indexOf(':');
        String content = text.substring(lastIndexOfSender + 1);
        char[] chars = text.toCharArray();

        for (int i = 0; i <= lastIndexOfSender; i++)
        {
            if (chars[i] != '[' && chars[i] != ']')
                sender.append(chars[i]);
        }
        return new Message(sender.toString(), content);
    }

    private boolean handleCommands(String command) throws IOException
    {
        switch (command.toUpperCase())
        {
            case "REGISTER":
                register();
                return true;
            case "READY":
                if (isRegistered)
                {
                    registerHandler.addReadyClient(this);
                    dataOutputStream.writeUTF("You are added to the game's members!\n" +
                            "Waiting for others to join, the game will start in just a moment...");
                    System.out.println("[" + username + "] is ready to play");
                }
                else dataOutputStream.writeUTF("Register first!");
                return true;
            case "REVIEW":
                dataOutputStream.writeUTF(server.getGameData().loadMessages());
                return true;
            case "HELP":
                dataOutputStream.writeUTF("""
                        Game's commands are:
                        REGISTER
                        READY
                        REVIEW (for getting the chat history)
                        EXIT (for exiting the game)""");
                return true;
            case "EXIT":
                dataOutputStream.writeUTF("Bye");
                return false;
            case "DISCONNECT":
                server.broadcast(new Message("Left the game", username));
                server.getUsers().remove(this);
            default:
                if (isRegistered)
                    server.broadcast(new Message(command, username));
                return true;
        }
    }

    public void terminate()
    {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
