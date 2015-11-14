package tungus.games.graphchanger.game.opponent;

import tungus.games.graphchanger.game.graph.editing.moves.Move;
import tungus.games.graphchanger.game.graph.editing.moves.MoveListener;

/**
 * An interface for communicating with the opponent, either AI or remote player.
 */
public interface OpponentConnection extends MoveListener {

    /**
     * @return Whether this connection is capable of sending and receiving messages. If false, the connection just acts as a dummy.
     */
    public boolean isActive();

    /**
     * @return Whether the opponent is ready to receive the next Move
     */
    public boolean shouldSend();

    /**
     * Pass all Moves received from the opponent since the last call to a given MoveListener
     */
    public void processReceived(MoveListener listener);

    /**
     * Sends the Move received since it was last called. If there was none, sends a confirmation of that. <br>
     * Should be called exactly once per tick.
     */
    public void send();

    /**
     * Sets the Move to be sent to the opponent this tick when send() is called.
     * Should be called at most once per tick, otherwise only the last Move will be sent.
     */
    @Override
    public void addMove(Move m);

    /**
     * Sets the Move to be sent to the opponent this tick when send() is called.
     * Should only be called with incrementing tickIDs, at most once per tick.
     */
    @Override
    public void addMove(Move m, int tickID);
}
