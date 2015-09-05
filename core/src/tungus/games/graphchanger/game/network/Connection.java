package tungus.games.graphchanger.game.network;

import com.badlogic.gdx.Gdx;
import tungus.games.graphchanger.game.graph.editing.moves.Move;
import tungus.games.graphchanger.game.graph.editing.moves.MoveListener;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Handles sending and receiveing Moves to/from the remote player.
 */
public class Connection implements MoveListener, NetworkCommunicator.NetworkTokenListener {

    private final NetworkCommunicator comm;

    private final Queue<Move> received = new LinkedList<Move>();
    private int nextReceivedTickNum = 1;

    private Move toSend = Move.NONE;
    private int nextSentTickNum = 1;

    public Connection(NetworkCommunicator comm) {
        this.comm = comm;
        comm.addListener(this);
    }

    @Override
    public boolean receivedMessage(int[] m) {
        Move move = Move.fromMessage(m);
        if (move == null)
            return false;
        synchronized (received) {
            received.add(move);
        }
        return true;
    }

    /**
     * Sends the accumulated {@link Move Moves} received from the InputStream to the given listener.
     * @param listener The MoveListener to give the Moves to
     */
    public void processReceived(MoveListener listener) {
        while (!received.isEmpty()) {
            synchronized (received) {
                Move m = received.remove();
                if (m != Move.NONE) {
                    listener.addMove(m, nextReceivedTickNum);
                    Gdx.app.log("COMM", "Tick " + nextReceivedTickNum + ": Received Move [" + m.toString() + "]");
                }
                nextReceivedTickNum++;
            }
        }
    }

    /**
     * Should be called at most once per tick, otherwise only the last Move will be sent.
     */
    @Override
    public void addMove(Move m) {
        toSend = m;
    }

    @Override
    public void addMove(Move m, int tickID) {
        if (tickID != nextSentTickNum)
            throw new IllegalStateException("Can only send ticks with incrementing IDs!");
        toSend = m;
    }

    public boolean shouldSend() {
        return nextReceivedTickNum >= nextSentTickNum;
    }

    /**
     * Sends the {@link Move} received since it was last called. If there was none, sends a confirmation of that. <br>
     * Should be called exactly once per tick.
     */
    public void send() {
        comm.write(toSend);
        if (toSend != Move.NONE)
            Gdx.app.log("COMM", "Tick " + nextSentTickNum + ": Sent Move [" + toSend.toString() + "]");
        toSend = Move.NONE;
        nextSentTickNum++;
    }

    public boolean isActive() {
        return comm.isConnected();
    }
}
