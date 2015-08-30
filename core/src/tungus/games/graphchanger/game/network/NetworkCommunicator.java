package tungus.games.graphchanger.game.network;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles communicating with a remote game. <br>
 * Offers received messages to a list of listeners until one consumes it (like InputMultiplexer).
 */
public class NetworkCommunicator {

    public static interface NetworkTokenListener {

        /**
         * Called when a message was received: a stream of bytes terminated by a delimiter.
         *
         * @param m The bytes received. Only valid data up to the first delimiter.
         * @return Whether the listener consumed the event or it can be offered to other listeners as well.
         */
        public boolean receivedMessage(int[] m);
    }

    public static interface Writable {

        public void writeTo(OutputStream out) throws IOException;
    }

    public static final int DELIM = 255;

    private static final int MAX_TOKEN_LENGTH = 100;

    private final InputStream in;
    private final OutputStream out;

    private final int[] readMessage = new int[MAX_TOKEN_LENGTH];
    private final List<NetworkTokenListener> listeners = new LinkedList<NetworkTokenListener>();

    private volatile boolean connected = true;
    private final Runnable reader = new Runnable() {
        @Override
        public void run() {
            while (connected) {
                try {
                    int i = 0;
                    do {
                        readMessage[i] = in.read();
                    } while (readMessage[i++] != DELIM);
                    synchronized (listeners) {
                        // Indexed loop so that called methods can swap the listeners in the list (when starting new game)
                        //noinspection ForLoopReplaceableByForEach
                        for (int j = 0; j < listeners.size(); j++) {
                            NetworkTokenListener l = listeners.get(j);
                            if (l.receivedMessage(readMessage)) {
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    Gdx.app.log("CONNECTION", "Failed to send, aborting connection");
                    e.printStackTrace();
                    abortConnection();
                }
            }
            Gdx.app.log("CONNECTION", "Reader thread stopped");
        }
    };

    public NetworkCommunicator(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
        Thread readerThread = new Thread(reader);
        readerThread.setName("Network read thread");
        readerThread.start();
    }

    public void addListener(int index, NetworkTokenListener l) {
        synchronized (listeners) {
            listeners.add(index, l);
        }
    }

    public void addListener(NetworkTokenListener l) {
        synchronized (listeners) {
            addListener(listeners.size(), l);
        }
    }

    public void removeListener(NetworkTokenListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    public void removeListener(int i) {
        synchronized (listeners) {
            listeners.remove(i);
        }
    }

    public synchronized void write(Writable toSend) {
        if (!connected) return;
        try {
            toSend.writeTo(out);
            out.write(DELIM);
        } catch (IOException e) {
            Gdx.app.log("CONNECTION", "Failed to send, aborting connection");
            e.printStackTrace();
            abortConnection();
        }
    }

    private synchronized void abortConnection() {
        if (!connected) return;
        try {
            connected = false;
            in.close();
            out.close();
            Gdx.app.log("CONNECTION", "Connection aborted. Any further messages WILL NOT BE SENT.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        abortConnection();
    }
}
