package tungus.games.graphchanger.game.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.game.editor.Move;
import tungus.games.graphchanger.game.editor.MoveListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Handles communication over network.
 */
public class Connection implements MoveListener {

    private static final Move NULL_MOVE = new Move(0,0,false); // Used because Queue doesn't support null elements
    private final InputStream in;
    private final OutputStream out;
    private final MoveEncoder encoder = new MoveEncoder();
    private final IntReaderWriter intSender = new IntReaderWriter();

    private final Queue<Move> received = new ArrayDeque<Move>();
    private int nextReceivedTick = 0;

    private Move toSend = null;
    private int sentTicks = 0;

    private final Thread reader = new Thread() {
        @Override
        public void run() {
            while (true) {
                try {
                    int receivedCode = intSender.read(in);
                    synchronized(received) {
                        Move m = encoder.decode(receivedCode);
                        if (m != null) {
                            received.add(m);
                            Gdx.app.log("NETWORK", "Received code " + receivedCode + ", Move " + m.toString());
                        }
                        else
                            received.add(NULL_MOVE);
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
            int code = encoder.encode(toSend);
            intSender.write(out, code);
            if (toSend != null)
                Gdx.app.log("NETWORK", "Sent Move " + toSend.toString() + " as code " + code);
        } catch (RuntimeException e) {
            throw new GdxRuntimeException("Failed to send move", e);
        }
        toSend = null;
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
                if (m != NULL_MOVE) {
                    listener.addMove(m, nextReceivedTick);
                    Gdx.app.log("NETWORK", "Processing Move " + m.toString());
                }
                nextReceivedTick++;
            }
        }
    }
}
