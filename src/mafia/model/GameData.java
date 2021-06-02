package mafia.model;

import mafia.model.element.*;
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
    private Winner winner;
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
        winner = Winner.UNKNOWN;
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

    public ArrayList<Player> getMafias()
    {
        ArrayList<Player> mafias = new ArrayList<>();
        for (Player p : alivePlayers)
        {
            if (isMafia(p))
                mafias.add(p);
        }
        return mafias;
    }

    public ArrayList<Player> getCitizens()
    {
        ArrayList<Player> citizens = new ArrayList<>();
        for (Player p : alivePlayers)
        {
            if (isCitizen(p))
                citizens.add(p);
        }
        return citizens;
    }

    public boolean isMafia(Player player)
    {
        Role role = player.getRole();
        return role == Role.MAFIA || role == Role.GODFATHER || role == Role.LECTOR;
    }

    public boolean isCitizen(Player player)
    {
        Role role = player.getRole();
        return role == Role.CITIZEN || role == Role.DOCTOR || role == Role.DETECTIVE
                || role == Role.MAYOR || role == Role.SNIPER
                || role == Role.ARNOLD || role == Role.THERAPIST;
    }

}
