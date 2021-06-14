package mafia.view;

/**
 * This class is responsible to display messages to a client with a nice look
 * @author KIMIA
 * @version 1.0
 */
public class Display
{
    /**
     * Prints messages in the console
     * @param text a text to display
     */
    public static void print(String text)
    {
        // check if the text is a message and not an error message or sth like that
        if (text.contains("[") && text.contains("]"))
        {
            String sender = text.substring(0, text.indexOf(":"));
            String[] lines = text.split("\n");

            System.out.print("\t\t\t\t\t");
            System.out.print(sender);
            System.out.println(lines[0].substring(sender.length()));

            for (int i = 1; i < lines.length; i++)
            {
                System.out.print("\t\t\t\t\t");
                for (int j = 0; j < sender.length() + 2; j++)
                    System.out.print(" ");
                System.out.println(lines[i]);
            }
            System.out.println();
        }
        else System.out.println(text);
    }

    /**
     * Displays the message history
     * @param history message history
     */
    public static void displayHistory(String history) {
        System.out.println(history);
    }
}
