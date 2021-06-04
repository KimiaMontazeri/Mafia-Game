package mafia.model.element;

import java.util.HashSet;
import java.util.Set;

public class Election
{
    private final Set<Vote> votes;

    public Election() {
        votes = new HashSet<>();
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public void addVote(Vote vote) {
        votes.add(vote);
    }

    /**
     * Calculates the final result of the election
     */
    public void calFinalResult()
    {

    }

    @Override
    public String toString() {
        return "Election{" +
                "votes=" + votes +
                '}';
    }
}
