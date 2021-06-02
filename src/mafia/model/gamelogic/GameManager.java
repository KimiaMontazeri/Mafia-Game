package mafia.model.gamelogic;


import mafia.model.GameData;
import mafia.model.element.Phase;
import mafia.model.element.Player;
import mafia.model.element.Vote;

import java.util.HashSet;
import java.util.Set;

public class GameManager // rules the game, gives the client handler the permission to send particular messages
{
    private final GameData gameData;
    private Phase gameMood = Phase.NOT_STARTED;

    private boolean electionIsOn = false;
    private final Set<Vote> votes;
    private Player lastKilledPlayer;

    public GameManager()
    {
        gameData = GameData.getInstance();
        votes = new HashSet<>();
    }
}
