package tungus.games.graphchanger.game.opponent;

import tungus.games.graphchanger.NetworkCommunicator;
import tungus.games.graphchanger.game.graph.editing.moves.Move;
import tungus.games.graphchanger.game.graph.editing.moves.MoveListener;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Handles sending and receiveing Moves to/from the remote player.
 */
public class RemotePlayerConnection implements OpponentConnection, NetworkCommunicator.NetworkTokenListener {

    private final NetworkCommunicator comm;

    private final Queue<Move> received = new LinkedList<Move>();
    private int nextReceivedTickNum = 1;

    private Move toSend = Move.NONE;
    private int nextSentTickNum = 1;

    public RemotePlayerConnection(NetworkCommunicator comm) {
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

    @Override
    public void processReceived(MoveListener listener) {
        while (!received.isEmpty()) {
            synchronized (received) {
                Move m = received.remove();
                listener.addMove(m, nextReceivedTickNum);
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

    @Override
    public boolean shouldSend() {
        return nextReceivedTickNum >= nextSentTickNum;
    }

    @Override
    public void send() {
        comm.write(toSend);
        toSend = Move.NONE;
        nextSentTickNum++;
    }

    @Override
    public boolean isActive() {
        return comm.isConnected();
    }
}
