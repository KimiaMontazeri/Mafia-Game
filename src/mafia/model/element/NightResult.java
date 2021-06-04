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

    public void setArnoldHadInquiry(boolean arnoldHadInquiry) {
        this.arnoldHadInquiry = arnoldHadInquiry;
    }

    public void addMurder(Player killedPlayer, Role murderer)
    {
        if (murderer == MAFIA || murderer == GODFATHER || murderer == SNIPER)
            murders.put(killedPlayer, murderer);
    }

    public void addHeal(Player healedPlayer, Role healer)
    {
        if (murders.containsKey(healedPlayer))
        {
            murders.remove(healedPlayer);
            heals.put(healedPlayer, healer);
        }
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
        for (Player killedPlayer : murders.keySet())
            str.append(killedPlayer.getUsername()).append(" ");
        str.append("'s body(ies) was/were found last night 😲!\n");

        // checking if the list is empty because no one may get removed from the game at night
        if (!removedPlayers.isEmpty())
        {
            for (Player removedPlayer : removedPlayers)
                str.append(removedPlayer.getUsername()).append(" ");
            str.append(" got removed from the game!");
        }
        return str.toString();
    }
}
