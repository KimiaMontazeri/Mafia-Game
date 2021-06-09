package mafia.view;

public class Display
{
    public static void print(String text)
    {
        // check if the text is a message and not an error message or sth like that
        if (text.contains("[") && text.contains("]"))
        {
//            String sender = text.substring(0, text.indexOf("]"));
            String sender = text.substring(0, text.indexOf(":"));
            String[] lines = text.split("\n");

            System.out.print("\t\t\t\t\t");
            System.out.print(sender);
            System.out.println(lines[0].substring(sender.length()));
//            System.out.println(lines[0]);

            for (int i = 1; i < lines.length; i++)
            {
                System.out.print("\t\t\t\t\t");
                for (int j = 0; j < sender.length() + 2; j++) // used to be 3
                    System.out.print(" ");
                System.out.println(lines[i]);
            }
            System.out.println();
        }
        else System.out.println(text);
    }

    public static void displayHistory(String history) {
        System.out.println(history);
    }
}
