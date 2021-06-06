package mafia.model.utils;

import mafia.model.element.Message;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is responsible for saving the game's messages into a file
 * and reading from the file that contains the saved messages
 */
public class MessageAccessor
{
    private final ArrayList<Message> messages;
    private final String fileAddress;

    public MessageAccessor() {
        messages = new ArrayList<>();
        fileAddress = "data/messages.txt";
    }

    public void addMsg(Message msgToAdd)
    {
        messages.add(msgToAdd);
        try (FileWriter fileWriter = new FileWriter(fileAddress)) {
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write(msgToAdd.toString());
//            bw.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called when the game is over
     * and we want to send all the players a review of what has happened in the game
     * @return all the messages
     */
    public String loadGameMessages()
    {
        StringBuilder messages = new StringBuilder();
        try (FileReader fileReader = new FileReader(fileAddress)) {
            BufferedReader br = new BufferedReader(fileReader);
            String line;

            while ( (line = br.readLine()) != null )
                messages.append(line).append("\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages.toString();
    }

    public Message getLastMsg() {
        return messages.get(messages.size() - 1);
    }
}
