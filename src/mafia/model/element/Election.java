package mafia.model.element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Election
{
    private final HashMap<String, String> votes;   // voter's username -> candidate's username

    public Election() {
        votes = new HashMap<>();
    }

    public HashMap<String, String> getVotes() {
        return votes;
    }

    public void addVote(String voter, String candidate) {
        votes.put(voter, candidate);
    }

    /**
     * Calculates the final result of the election
     * @return the username of the final candidate
     */
    public String calFinalResult()
    {
//        Set<String> candidates = new HashSet<>(votes.values());
        // TODO complete this method
        return null;
    }

}
