package mafia.model.element;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Message implements Serializable
{
    private final String text;
    private final String sender;
    private final Set<Role> receivers;

    public Message(String text, String sender, Role... receivers)
    {
        this.text = text;
        this.sender = sender;
        this.receivers = new HashSet<>();
        this.receivers.addAll(Arrays.asList(receivers));
    }

    public String getText() {
        return text;
    }

    public Set<Role> getReceivers() {
        return receivers;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "[" + sender + "]: " + text + "\n";
    }
}
