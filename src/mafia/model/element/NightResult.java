package mafia.model.element;

import static mafia.model.element.Role.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NightResult
{
    private final HashMap<Player, Role> murders; // killed player -> murderer
    private final HashMap<Player, Role> heals; // healed player -> normal doctor/doctor lector
    private final Set<Player> removedPlayers;
    private boolean arnoldHadInquiry = false;
    private Player silencedPlayer;

    public NightResult() {
        murders = new HashMap<>();
        heals = new HashMap<>();
        removedPlayers = new HashSet<>();
    }

    public HashMap<Player, Role> getMurders() {
        return murders;
    }

    public HashMap<Player, Role> getHeals() {
        return heals;
    }

    public Set<Player> getRemovedPlayers() {
        return removedPlayers;
    }

    public boolean arnoldHadInquiry() {
        return arnoldHadInquiry;
    }

    public Player getSilencedPlayer() {
        return silencedPlayer;
    }

    public void setArnoldHadInquiry(boolean arnoldHadInquiry) {
        this.arnoldHadInquiry = arnoldHadInquiry;
    }

    public void setSilencedPlayer(Player silencedPlayer) {
        this.silencedPlayer = silencedPlayer;
    }

    public boolean isProtectedByLector(Player player)
    {
        if (heals.containsKey(player))
            return heals.get(player) == LECTOR;
        return false;
    }

    public void addMurder(Player killedPlayer, Role murderer)
    { // the murderer has to be checked (they can only be in mafia team or a sniper)
        if (murderer == MAFIA || murderer == GODFATHER)
            murders.put(killedPlayer, murderer);
        else if (murderer == SNIPER)
        {
            if (!heals.containsKey(killedPlayer)) // the sniper won't be able to kill a protected mafia
                murders.put(killedPlayer, murderer);
        }
    }

    public void addHeal(Player healedPlayer, Role healer)
    {
        if (murders.containsKey(healedPlayer) && healer == DOCTOR)
        {
            murders.remove(healedPlayer);
            heals.put(healedPlayer, healer);
        }
        else if (healer == LECTOR)
            heals.put(healedPlayer, healer);
    }

    public void addRemovedPlayer(Player player) {
        removedPlayers.add(player);
    }

    public boolean hasSniperKill()
    {
        for (Role murderer : murders.values())
        {
            if (murderer == SNIPER)
                return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        if (murders.size() == 0)
            str.append("Nobody got killed last night :|");

        else
        {
            str.append("We found the body/bodies of [ ");
            for (Player killedPlayer : murders.keySet())
                str.append(killedPlayer.getUsername()).append(" ");
            str.append("] last night 😲");
        }

        // checking if the list is empty because no one may get removed from the game at night
        if (!removedPlayers.isEmpty())
        {
            for (Player removedPlayer : removedPlayers)
                str.append(removedPlayer.getUsername()).append(" ");
            str.append("got removed from the game!");
        }
        if (silencedPlayer != null)
            str.append("\n").append(silencedPlayer.getUsername()).append(" got silenced for the next day");
        return str.toString();
    }
}
