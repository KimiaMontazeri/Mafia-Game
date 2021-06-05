package mafia.view;

import mafia.model.element.Message;

public class Display
{
    public static void print(Message msg)
    {
        System.out.print("\n" + msg);
    }

    public static void print(String text)
    {
        System.out.print(text);
    }
}
