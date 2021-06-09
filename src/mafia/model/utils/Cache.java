package mafia.model.utils;

import java.io.*;

public class Cache
{
    private final String fileAddress;

    public Cache (String fileAddress) {
        this.fileAddress = "data/" + fileAddress + ".txt";
    }

    public void addMessage(String text)
    {
        try (FileWriter fw = new FileWriter(fileAddress, true)) {
            fw.write(text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getHistory()
    {
        StringBuilder result = new StringBuilder();
        String line = "";
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
