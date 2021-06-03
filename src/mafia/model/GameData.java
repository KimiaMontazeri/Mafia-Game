package mafia.model;

import mafia.model.element.*;
import static mafia.model.element.Role.*;
import static mafia.model.element.Phase.*;
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

    public boolean lectorHasHealedHimself;
    public boolean doctorHasHealedHimself;
    public boolean mafiaHasShootArnold;
    private int arnoldInquiries;

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
        currentPhase = NOT_STARTED;
        winner = Winner.UNKNOWN;

        hasLector = hasMayor = hasArnold = hasSniper = hasTherapist = false;
        lectorHasHealedHimself = doctorHasHealedHimself = mafiaHasShootArnold = false;
        arnoldInquiries = 0;

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

    public int getArnoldInquiries() {
        return arnoldInquiries;
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

    public boolean incrementArnoldInquiries()
    {
        if (arnoldInquiries >= 0 && arnoldInquiries < 2) {
            arnoldInquiries++;
            return true;
        }
        return false;
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
        return role == MAFIA || role == GODFATHER || role == LECTOR;
    }

    public boolean isCitizen(Player player)
    {
        Role role = player.getRole();
        return role == CITIZEN || role == DOCTOR || role == DETECTIVE
                || role == MAYOR || role == SNIPER
                || role == ARNOLD || role == THERAPIST;
    }

}
