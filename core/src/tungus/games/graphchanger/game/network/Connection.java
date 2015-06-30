package tungus.games.graphchanger.game.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.game.graph.editor.Move;
import tungus.games.graphchanger.game.graph.editor.MoveListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Handles communication over network.
 */
public class Connection implements MoveListener {

    private final InputStream in;
    private final OutputStream out;

    private final Queue<Move> received = new LinkedList<Move>();
    private int nextReceivedTick = 0;

    private Move toSend = Move.NULL;
    private int sentTicks = 0;

    private final Thread reader = new Thread() {
        @Override
        public void run() {
            while (true) {
                try {
                    Move m = Move.read(in);
                    synchronized(received) {
                        received.add(m);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    };

    public Connection(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
        reader.start();
    }

    @Override
    public void addMove(Move m) {
        toSend = m;
    }

    @Override
    public void addMove(Move m, int tickID) {
        if (tickID != sentTicks)
            throw new IllegalStateException("Can only send ticks with incrementing IDs!");
        toSend = m;
    }

    /**
     * Sends the {@link Move} received since it was last called. If there was none, sends a confirmation of that. <br>
     * Should be called exactly once per tick.
     */
    public void send() {
        try {
            toSend.write(out);
            if (toSend != Move.NULL)
                Gdx.app.log("NETWORK", "Tick " + sentTicks + ": Sent Move " + toSend.toString());
        } catch (RuntimeException e) {
            throw new GdxRuntimeException("Failed to send move", e);
        }
        toSend = Move.NULL;
        sentTicks++;
    }

    /**
     * Sends the accumulated {@link Move Moves} received from the InputStream to the given listener.
     * @param listener The MoveListener to give the Moves to
     */
    public void processReceived(MoveListener listener) {
        while(!received.isEmpty()) {
            synchronized (received) {
                Move m = received.remove();
                if (m != Move.NULL) {
                    listener.addMove(m, nextReceivedTick);
                    Gdx.app.log("NETWORK", "Tick " + nextReceivedTick + ": Processing Move " + m.toString());
                }
                nextReceivedTick++;
            }
        }
    }
}
