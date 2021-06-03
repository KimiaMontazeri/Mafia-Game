package mafia.model;

import mafia.model.element.*;
import mafia.model.utils.MessageAccessor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class GameData implements Serializable // used for saving the game
{
    private static GameData instance;
    private HashSet<Player> deadPlayers;
    private HashSet<Player> alivePlayers;
    private Phase currentPhase;
    private Vote lastVote;
    private Winner winner;
    private ArrayList<Message> oldMessages; // add each message to this arrayList
    private final MessageAccessor messageAccessor;

    public boolean hasLector, hasMayor, hasArnold, hasSniper, hasTherapist;

    public static GameData getInstance()
    {
        if (instance == null)
            return new GameData();
        return instance;
    }

    private GameData()
    {
        deadPlayers = new HashSet<>();
        alivePlayers = new HashSet<>();
        oldMessages = new ArrayList<>();
        currentPhase = Phase.NOT_STARTED;
        winner = Winner.UNKNOWN;
        hasLector = hasMayor = hasArnold = hasSniper = hasTherapist = false;
        messageAccessor = new MessageAccessor();
    }

    // getters and setters
    public HashSet<Player> getDeadPlayers() {
        return deadPlayers;
    }

    public HashSet<Player> getAlivePlayers() {
        return alivePlayers;
    }

    public Phase getCurrentMood() {
        return currentPhase;
    }

    public Vote getLastVote() {
        return lastVote;
    }

    public Winner getWinner() {
        return winner;
    }

    public ArrayList<Message> getOldMessages() {
        return oldMessages;
    }

    public void setAlivePlayers(ArrayList<Player> players)
    {
        if (players != null)
            alivePlayers.addAll(players);
    }

    public void setCurrentMood(Phase currentMood) {
        this.currentPhase = currentMood;
    }

    public void setLastVote(Vote lastVote) {
        if (lastVote != null)
            this.lastVote = lastVote;
    }

    public void setWinner(Winner winner) {
        this.winner = winner;
    }

    public void saveMessages() {
        messageAccessor.saveMessages(oldMessages);
    }

    public void readOldMessages() {
        oldMessages = messageAccessor.readMessages();
    }

    public HashSet<Player> getMafias()
    {
        HashSet<Player> mafias = new HashSet<>();
        for (Player p : alivePlayers)
        {
            if (isMafia(p))
                mafias.add(p);
        }
        return mafias;
    }

    public HashSet<Player> getCitizens()
    {
        HashSet<Player> citizens = new HashSet<>();
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
