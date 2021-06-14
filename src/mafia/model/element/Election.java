package mafia.model.element;

import java.util.*;

/**
 * This class represents an election in the mafia game
 * @author KIMIA
 * @version 1.0
 */
public class Election
{
    private final HashMap<Player, Player> votes;   // voter -> candidate
    private Player finalCandidate;
    private boolean isMafiaElection;

    /**
     * Creates an election
     */
    public Election() {
        votes = new HashMap<>();
        isMafiaElection = false;
    }

    /**
     * @return final candidate
     */
    public Player getFinalCandidate() {
        return finalCandidate;
    }

    /**
     * @return List of all the votes
     */
    public HashMap<Player, Player> getVotes() {
        return votes;
    }

    /**
     * Adds a vote to the election if possible
     * @param voter the votes
     * @param candidate the candidate
     */
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

    /**
     * this method is used for when this election if a mafia type election
     * and the final candidate is selected manually by the godfather
     * @param finalCandidate final candidate of the election
     */
    public void setFinalCandidate(Player finalCandidate) {
        if (isMafiaElection)
            this.finalCandidate = finalCandidate;
    }

    /**
     * Changes the election to a mafia type election if the parameter is true
     * @param mafiaElection whether the election is mafia election
     */
    public void setMafiaElection(boolean mafiaElection) {
        isMafiaElection = mafiaElection;
    }

    /**
     * Calculates the final result of the election
     * Used for the day_elections (final result of mafia elections are determined by the godfather)
     * @return the username of the final candidate
     */
    public Player calFinalResult()
    {
        Map<Player, Integer> candidateResult = new HashMap<>();
        int numOfVotes;
        if (finalCandidate == null)
        {
            if (votes.size() == 0)
                return null;
            for (Player candidate : votes.values())
            {
                if (candidateResult.containsKey(candidate))
                {
                    numOfVotes = candidateResult.get(candidate);
                    candidateResult.replace(candidate, ++numOfVotes);
                }
                else candidateResult.put(candidate, 1);
            }

            return findMax(candidateResult);
        }
        return finalCandidate;
    }

    /**
     * @return the election result in string
     */
    public String toString()
    {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<Player, Player> entry : votes.entrySet())
        {
            result.append(entry.getKey().getUsername()).append(" chooses ").append(entry.getValue().getUsername());
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * Finds the candidate with the maximum number of votes
     * @param map a map of candidate and the number of votes they have
     * @return the candidate with the maximum number of votes
     */
    private Player findMax(Map<Player, Integer> map)
    {
        Player result = null;
        int max = 0;
        for (Map.Entry<Player, Integer> entry : map.entrySet())
        {
            if (entry.getValue() > max)
            {
                result = entry.getKey();
                max = entry.getValue();
            }
        }
        return result;
    }

}
