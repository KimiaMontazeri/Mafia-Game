package mafia.model.element;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Election //TODO change votes to -> HashMap<Player, Player>
{
    private final HashMap<Player, Player> votes;   // voter -> candidate
    private Player finalCandidate;
    private boolean isMafiaElection;

    public Election() {
        votes = new HashMap<>();
        isMafiaElection = false;
    }

    public Player getFinalCandidate() {
        return finalCandidate;
    }

    public HashMap<Player, Player> getVotes() {
        return votes;
    }

    public void addVote(Player voter, Player candidate)
    {
        if (voter != null && candidate != null && !voter.equals(candidate))
        {
            if (isMafiaElection && candidate.getRole() != Role.MAFIA) // check if a mafia is voting to kill a mafia
                votes.put(voter, candidate);
            else if (!isMafiaElection)
                votes.put(voter, candidate);
        }
    }

    public void setFinalCandidate(Player finalCandidate) {
        this.finalCandidate = finalCandidate;
    }

    public void setMafiaElection(boolean mafiaElection) {
        isMafiaElection = mafiaElection;
    }

    /**
     * Calculates the final result of the election
     * Used for the day_elections (final result of mafia elections are determined by the godfather)
     * @return the username of the final candidate
     */
    public Player calFinalResult() // TODO incomplete
    {
        HashMap<Player, Integer> candidateResult = new HashMap<>();
        if (finalCandidate == null)
        {
            for (Player candidate : votes.values())
            {
                if (candidateResult.containsKey(candidate))
                {

                }
                else
                {
                    candidateResult.put(candidate, 1);
                }
            }
        }
        return finalCandidate;
    }

    public String toString()
    {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<Player, Player> entry : votes.entrySet())
        {
            result.append(entry.getKey().getUsername()).append(" -> ").append(entry.getValue().getUsername());
            result.append("\n");
        }
        return result.toString();
    }

}
