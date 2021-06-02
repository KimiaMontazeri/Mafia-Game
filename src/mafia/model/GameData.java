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
    private ArrayList<Player> deadPlayers;
    private ArrayList<Player> alivePlayers;
    private Mood currentMood;
    private Vote lastVote;
    private ArrayList<Message> oldMessages; // add each message to this arrayList
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

    // getters and setters 
    public ArrayList<Player> getDeadPlayers() {
        return deadPlayers;
    }

    public ArrayList<Player> getAlivePlayers() {
        return alivePlayers;
    }

    public Mood getCurrentMood() {
        return currentMood;
    }

    public Vote getLastVote() {
        return lastVote;
    }

    public ArrayList<Message> getOldMessages() {
        return oldMessages;
    }

    public void setAlivePlayers(ArrayList<Player> alivePlayers) {
        if (alivePlayers != null)
            this.alivePlayers = alivePlayers;
    }

    public void setCurrentMood(Mood currentMood) {
        this.currentMood = currentMood;
    }

    public void setLastVote(Vote lastVote) {
        if (lastVote != null)
            this.lastVote = lastVote;
    }

    public void saveMessages() {
        messageAccessor.saveMessages(oldMessages);
    }

    public void readOldMessages() {
        oldMessages = messageAccessor.readMessages();
    }

}
