package mafia.model.element;

import java.io.Serializable;

/**
 * This class represents a message that has a context and a sender
 * @author KIMIA
 * @version 1.0
 */
public class Message implements Serializable
{
    private final String text;
    private final String sender;

    /**
     *
     * @param text message's context
     * @param sender sender's username
     */
    public Message(String text, String sender)
    {
        this.text = text;
        this.sender = sender;
    }

    /**
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * @return sender's username
     */
    public String getSender() {
        return sender;
    }

    /**
     * Converts the message into a string
     * @return the message in string form
     */
    @Override
    public String toString() {
        return "[" + sender + "]: " + text + "\n";
    }
}
