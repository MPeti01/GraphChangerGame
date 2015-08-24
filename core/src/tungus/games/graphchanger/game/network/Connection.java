package tungus.games.graphchanger.game.network;

import com.badlogic.gdx.Gdx;
import tungus.games.graphchanger.game.graph.editing.moves.Move;
import tungus.games.graphchanger.game.graph.editing.moves.MoveListener;

import java.io.IOException;
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

    private volatile boolean connected = true;

    private final Runnable reader = new Runnable() {
        @Override
        public void run() {
            while (connected) {
                try {
                    Move m = Move.read(in);
                    synchronized(received) {
                        received.add(m);
                    }
                } catch (Exception e) {
                    Gdx.app.log("CONNECTION", "Failed to read, aborting connection");
                    e.printStackTrace();
                    abortConnection();
                    break;
                }
            }
            Gdx.app.log("CONNECTION", "Reader thread stopped");
        }
    };
    private final Thread readerThread;


    public Connection(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
        readerThread = new Thread(reader);
        readerThread.setName("Move reader thread");
        readerThread.start();
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
        if (!connected) return;
        try {
            toSend.write(out);
            if (toSend != Move.NULL)
                Gdx.app.log("COMM", "Tick " + sentTicks + ": Sent Move " + toSend.toString());
        } catch (RuntimeException e) {
            Gdx.app.log("CONNECTION", "Failed to send, aborting connection");
            e.printStackTrace();
            abortConnection();
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
                    Gdx.app.log("COMM", "Tick " + nextReceivedTick + ": Processing Move " + m.toString());
                }
                nextReceivedTick++;
            }
        }
    }

    private void abortConnection() {
        try {
            connected = false;
            in.close();
            out.close();
            Gdx.app.log("CONNECTION", "Connection aborted. Any further Moves WILL NOT BE SENT.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        if (connected) {
            abortConnection();
        }
    }
}
