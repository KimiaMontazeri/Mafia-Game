package mafia.model;

import mafia.model.element.Message;
import mafia.model.element.Mood;
import mafia.model.element.Player;
import mafia.model.element.Vote;
import mafia.model.utils.MessageAccessor;

import java.io.Serializable;
import java.util.ArrayList;

public class GameData implements Serializable // used for saving the game
{
    private static GameData instance;
    public ArrayList<Player> deadPlayers;
    public ArrayList<Player> alivePlayers;
    public Mood currentMood;
    public Vote lastVote;
    public ArrayList<Message> oldMessages; // add each message to this arrayList
    private final MessageAccessor messageAccessor;

    public static GameData getInstance()
    {
        if (instance == null)
            return new GameData();
        return instance;
    }

    private GameData()
    {
        deadPlayers = new ArrayList<>();
        alivePlayers = new ArrayList<>();
        oldMessages = new ArrayList<>();
        currentMood = Mood.NOT_STARTED;
        messageAccessor = new MessageAccessor();
    }

    public void saveMessages() {
        messageAccessor.saveMessages(oldMessages);
    }

    public void readOldMessages() {
        oldMessages = messageAccessor.readMessages();
    }

}
