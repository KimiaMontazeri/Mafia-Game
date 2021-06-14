package mafia.model.utils;

import java.io.*;

/**
 * This class is responsible for saving each client's messages into a file
 * and reading all the saved messages from the file
 * @author KIMIA
 * @version 1.0
 */
public class Cache
{
    private final String fileAddress;

    /**
     * Creates a cache
     * @param fileAddress file address to save the file to
     */
    public Cache (String fileAddress) {
        this.fileAddress = "data/" + fileAddress + ".txt";
    }

    /**
     * Appends a text to the file
     * @param text text
     */
    public void addMessage(String text)
    {
        try (FileWriter fw = new FileWriter(fileAddress, true)) {
            fw.write(text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads all the messages from the file
     * @return the read messages
     */
    public String getHistory()
    {
        StringBuilder result = new StringBuilder();
        String line;
        try (FileReader fr = new FileReader(fileAddress);
             BufferedReader br = new BufferedReader(fr))
        {
            while ( (line = br.readLine()) != null)
                result.append(line).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
