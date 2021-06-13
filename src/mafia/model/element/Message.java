package mafia.model.element;

import java.io.Serializable;

public class Message implements Serializable
{
    private final String text;
    private final String sender;

    public Message(String text, String sender)
    {
        this.text = text;
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "[" + sender + "]: " + text + "\n";
    }
}
