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
    private static GameData instance = null;
    private final HashSet<Player> deadPlayers;
    private final HashSet<Player> alivePlayers;
    private Phase currentPhase;
    private Election lastElection;

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
        if (instance == null) {
            instance = new GameData();
            return instance;
        }
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

    public Election getLastElection() {
        return lastElection;
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

    public Message getLastMessage() {
        return oldMessages.get(oldMessages.size() - 1);
    }

    public void addMessage(Message message) {
        if (message != null)
            oldMessages.add(message);
    }

    public void setAlivePlayers(ArrayList<Player> players) {
        if (players != null)
            alivePlayers.addAll(players);
    }

    public void setCurrentMood(Phase currentMood) {
        this.currentPhase = currentMood;
    }

    public void setLastElection(Election lastElection) {
        this.lastElection = lastElection;
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
        if (arnoldInquiries == 0 || arnoldInquiries == 1) {
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

    public boolean isAsleep(String username)
    {
        for (Player p : alivePlayers)
        {
            // if p is the wanted player
            if (p.getUsername().equals(username))
                return p.isAsleep();
        }
        // if this line is reached, the given username is not in the game
        System.out.println(username + " is not in the database!");
        return false;
    }

    public boolean canSpeak(String username)
    {
        for (Player p : alivePlayers)
        {
            if (p.getUsername().equals(username))
                return p.canSpeak();
        }
        return false;
    }

    public boolean isAlive(String username)
    {
        for (Player p : alivePlayers)
        {
            if (p.getUsername().equals(username))
                return p.isAlive();
        }
        return false;
    }

    public Player findPlayer(String username)
    {
        for (Player p : alivePlayers)
        {
            if (p.getUsername().equals(username))
                return p;
        }
        return null;
    }

    public Player findPlayer(Role role)
    {
        for (Player p : alivePlayers)
        {
            if (p.getRole() == role)
                return p;
        }
        return null;
    }

}
