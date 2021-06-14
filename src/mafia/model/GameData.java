package mafia.model;

import mafia.model.element.*;
import static mafia.model.element.Role.*;
import static mafia.model.element.Phase.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class holds all the data and information of the game and is updated when its necessary
 * Only one object of this class can be made in the program
 * @author KIMIA
 * @version 1.0
 */
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
    private final ArrayList<Message> messages;

    public boolean hasLector, hasMayor, hasArnold, hasSniper, hasTherapist, hasGodfather, hasDoctor, hasDetective;

    /**
     * @return an instance of this class
     */
    public static GameData getInstance()
    {
        if (instance == null) {
            instance = new GameData();
            return instance;
        }
        return instance;
    }

    /**
     * Creates a GameData
     */
    private GameData()
    {
        deadPlayers = new HashSet<>();
        alivePlayers = new HashSet<>();
        currentPhase = NOT_STARTED;
        electionIsOn = false;
        winner = Winner.UNKNOWN;
        messages = new ArrayList<>();

        hasLector = hasMayor = hasArnold = hasSniper = hasTherapist = hasGodfather = hasDoctor = hasDetective = false;
        lectorHasHealedHimself = doctorHasHealedHimself = mafiaHasShootArnold = false;
        arnoldInquiries = 0;

    }

    /**
     * @return all the dead players
     */
    public HashSet<Player> getDeadPlayers() {
        return deadPlayers;
    }

    /**
     * @return all the alive players
     */
    public HashSet<Player> getAlivePlayers() {
        return alivePlayers;
    }

    /**
     * this method is used for when we want to notify the users about the list of all the current alive players
     * @return alive players' usernames
     */
    public String getListOfAlivePlayers()
    {
        StringBuilder result = new StringBuilder();
        for (Player p : alivePlayers)
            result.append(p.getUsername()).append("  ");
        return result.toString();
    }

    /**
     * this method is used for when we want to notify the users about the list of all the dead players
     * @return dead players' usernames
     */
    public String getListOfDeadPlayers()
    {
        StringBuilder result = new StringBuilder();
        for (Player p : deadPlayers)
            result.append(p.getUsername()).append("  ");
        return result.toString();
    }

    /**
     * @return current phase of the game
     */
    public Phase getCurrentPhase() {
        return currentPhase;
    }

    /**
     * @return true if an election is on right now
     */
    public boolean electionIsOn() {
        return electionIsOn;
    }

    /**
     * @return the last election that has been held
     */
    public Election getLastElection() {
        return lastElection;
    }

    /**
     * @return last night's result
     */
    public NightResult getLastNightResult() {
        return lastNightResult;
    }

    /**
     * @return winner of the game
     */
    public Winner getWinner() {
        return winner;
    }

    /**
     * @return last message of the game
     */
    public Message getLastMessage() {
        return messages.get(messages.size() - 1);
    }

    /**
     * Adds a new message to the game's message s
     * @param message message to add
     */
    public void addMessage(Message message) {
        if (message != null)
            messages.add(message);
    }

    /**
     * Sets the list of alive players
     * @param players a list of players
     */
    public void setAlivePlayers(ArrayList<Player> players) {
        if (players != null)
            alivePlayers.addAll(players);
    }

    /**
     * Sets the current phase of the game
     * @param currentPhase current game's phase
     */
    public void setCurrentPhase(Phase currentPhase) {
        this.currentPhase = currentPhase;
    }

    /**
     * @param electionIsOn if an election is on
     */
    public void setElectionIsOn(boolean electionIsOn) {
        this.electionIsOn = electionIsOn;
    }

    /**
     * Sets the last election
     * @param lastElection last election
     */
    public void setLastElection(Election lastElection) {
        this.lastElection = lastElection;
    }

    /**
     * Sets the last night result
     * @param lastNightResult last night result
     */
    public void setLastNightResult(NightResult lastNightResult) {
        this.lastNightResult = lastNightResult;
    }

    /**
     * Sets the game's winner
     * @param winner winner
     */
    public void setWinner(Winner winner) {
        this.winner = winner;
    }

    /**
     * @return alive mafias
     */
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

    /**
     * @return alive citizens
     */
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

    /**
     * Checks whether the player is a mafia
     * @param player player to check their role
     * @return true if the player is mafia
     */
    public boolean isMafia(Player player)
    {
        Role role = player.getRole();
        return role == MAFIA || role == GODFATHER || role == LECTOR;
    }

    /**
     * Checks whether the player is a citizen
     * @param player player to check their role
     * @return true if the player is citizen
     */
    public boolean isCitizen(Player player)
    {
        Role role = player.getRole();
        return role == CITIZEN || role == DOCTOR || role == DETECTIVE
                || role == MAYOR || role == SNIPER
                || role == ARNOLD || role == THERAPIST;
    }

    /**
     * Checks whether a player with the given username is awake
     * @param username username of the player
     * @return true if the player is awake
     */
    public boolean isAwake(String username)
    {
        // check if the player is in the dead players
        for (Player p : alivePlayers)
        {
            if (p.getUsername().equals(username))
                return !p.isAsleep();
        }
        // dead players are awake as long as the game continues, and they can receive all the messages but they cannot talk
        for (Player p : deadPlayers)
        {
            if (p.getUsername().equals(username))
                return true;
        }
        return false;
    }

    /**
     * Checks whether a player with the given username can speak
     * @param username username of the player
     * @return true if the player can speak
     */
    public boolean canSpeak(String username)
    {
        for (Player p : alivePlayers)
        {
            if (p.getUsername().equals(username))
                return p.canSpeak();
        }
        return false;
    }

    /**
     * Finds a player with the given username
     * @param username username of the player
     * @return found player
     */
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

    /**
     * Finds a player with the given role
     * @param role role of the player
     * @return found player
     */
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
