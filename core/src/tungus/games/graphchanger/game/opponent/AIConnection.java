package tungus.games.graphchanger.game.opponent;

import tungus.games.graphchanger.game.graph.editing.moves.Move;
import tungus.games.graphchanger.game.graph.editing.moves.MoveListener;

/**
 * Connection for interfacing with the AI.
 */
public class AIConnection implements OpponentConnection {

    private final AI ai;

    private Move toSend = Move.NONE;
    private boolean playerSentMove = false;

    public AIConnection(AI ai) {
        this.ai = ai;
    }

    @Override
    public void addMove(Move m) {
        toSend = m;
    }

    @Override
    public void addMove(Move m, int tickID) {
        toSend = m;
    }

    @Override
    public void send() {
        ai.nextPlayerMove(toSend);
        toSend = Move.NONE;
        playerSentMove = true;
    }

    @Override
    public void processReceived(MoveListener listener) {
        if (playerSentMove) {
            listener.addMove(ai.getNextMove());
            playerSentMove = false;
        }
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean shouldSend() {
        return true;
    }
}
