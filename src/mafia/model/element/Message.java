package mafia.model.element;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Message
{
    private final String text;
    private final Player sender;
    private final Set<Role> receivers;

    public Message(String text,  Player sender, Role... receivers)
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

    public Player getSender() {
        return sender;
    }
}
