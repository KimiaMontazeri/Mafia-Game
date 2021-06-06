package mafia.model;

import mafia.model.element.*;
import static mafia.model.element.Role.*;
import static mafia.model.element.Phase.*;
import mafia.model.utils.MessageAccessor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

// TODO add the message methods to the class message accessor (store messages in a linked list)
public class GameData implements Serializable // used for saving the game
{
    private static GameData instance = null;
    private final HashSet<Player> deadPlayers;
    private final HashSet<Player> alivePlayers;
    private Phase currentPhase;
    private boolean electionIsOn;
    private Election lastElection;
    private NightResult lastNightResult;

    public boolean lectorHasHealedHimself;
    public boolean doctorHasHealedHimself;
    public boolean mafiaHasShootArnold;
    public int arnoldInquiries;

    private Winner winner;
    private ArrayList<Message> oldMessages; // add each message to this arrayList
    private final MessageAccessor messageAccessor;

    public boolean hasLector, hasMayor, hasArnold, hasSniper, hasTherapist, hasGodfather, hasDoctor, hasDetective;

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
        electionIsOn = false;
        winner = Winner.UNKNOWN;

        hasLector = hasMayor = hasArnold = hasSniper = hasTherapist = hasGodfather = hasDoctor = hasDetective = false;
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

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public boolean electionIsOn() {
        return electionIsOn;
    }

    public Election getLastElection() {
        return lastElection;
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

    public void setCurrentPhase(Phase currentMood) {
        this.currentPhase = currentMood;
    }

    public void setElectionIsOn(boolean electionIsOn) {
        this.electionIsOn = electionIsOn;
    }

    public void setLastElection(Election lastElection) {
        this.lastElection = lastElection;
    }

    public void setLastNightResult(NightResult lastNightResult) {
        this.lastNightResult = lastNightResult;
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
        return role == MAFIA || role == GODFATHER || role == LECTOR;
    }

    public boolean isCitizen(Player player)
    {
        Role role = player.getRole();
        return role == CITIZEN || role == DOCTOR || role == DETECTIVE
                || role == MAYOR || role == SNIPER
                || role == ARNOLD || role == THERAPIST;
    }

    public boolean isAwake(String username)
    {
        for (Player p : alivePlayers)
        {
            if (p.getUsername().equals(username))
                return !p.isAsleep();
        }
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

    public Player findPlayer(String username)
    {
        if (username != null)
        {
            for (Player p : alivePlayers)
            {
                if (p.getUsername().equals(username))
                    return p;
            }
        }
        return null;
    }

    public Player findPlayer(Role role)
    {
        if (role != null)
        {
            for (Player p : alivePlayers)
            {
                if (p.getRole() == role)
                    return p;
            }
        }
        return null;
    }

}
