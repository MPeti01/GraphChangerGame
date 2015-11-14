package tungus.games.graphchanger;

import com.badlogic.gdx.Application;
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

    private volatile boolean connected;

    private boolean logging = false;

    private final Runnable reader = new Runnable() {
        @Override
        public void run() {
            while (connected) {
                try {
                    int i = 0;
                    do {
                        readMessage[i] = in.read();
                    } while (readMessage[i++] != DELIM);
                    if (logging)
                        Gdx.app.log("CONNECTION", "Message received (length = " + (i-1) + ")");
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
                    Gdx.app.log("CONNECTION", "Failed to read, aborting connection");
                    e.printStackTrace();
                    abortConnection();
                } catch (ArrayIndexOutOfBoundsException e) {
                    Gdx.app.setLogLevel(Application.LOG_ERROR);
                    Gdx.app.setLogLevel(Application.LOG_DEBUG);
                    if (readMessage[MAX_TOKEN_LENGTH-1] == -1) {
                        Gdx.app.log("CONNECTION", "Reading -1, disconnected - aborting connection");
                        abortConnection();
                    } else {
                        Gdx.app.log("CONNECTION", "Message too long, ignoring!");
                    }
                }
            }
            Gdx.app.log("CONNECTION", "Reader thread stopped");
        }
    };
    public NetworkCommunicator(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
        connected = (in != null && out != null);
        if (connected) {
            Thread readerThread = new Thread(reader);
            readerThread.setName("Network read thread");
            readerThread.start();
        }
    }

    /**
     * Creates a not-actually-connected dummy instance helpful for handling SP and MP together.
     */
    public static NetworkCommunicator dummy() {
        return new NetworkCommunicator(null, null);
    }

    public void addListener(int index, NetworkTokenListener l) {
        synchronized (listeners) {
            listeners.add(index, l);
        }
        Gdx.app.log("CONNECTION", "Added listener to index " + index + " (size = " + listeners.size() + ")");
    }

    public void addListener(NetworkTokenListener l) {
        synchronized (listeners) {
            addListener(listeners.size(), l);
        }
    }

    public void removeListener(NetworkTokenListener l) {
        boolean removed;
        synchronized (listeners) {
            removed = listeners.remove(l);
        }
        if (removed)
            Gdx.app.log("CONNECTION", "Removed listener");
    }

    public void removeListener(int i) {
        synchronized (listeners) {
            listeners.remove(i);
        }
        Gdx.app.log("CONNECTION", "Removed listener from index " + i);
    }

    public void clearListeners() {
        listeners.clear();
        Gdx.app.log("CONNECTION", "Listeners cleared");
    }

    public synchronized void write(Writable toSend) {
        if (!connected) return;
        try {
            toSend.writeTo(out);
            out.write(DELIM);
            if (logging)
                Gdx.app.log("CONNECTION", "Sent message (obj)");
        } catch (IOException e) {
            Gdx.app.log("CONNECTION", "Failed to send, aborting connection");
            e.printStackTrace();
            abortConnection();
        }
    }

    public synchronized void write(int... data) {
        if (!connected) return;
        try {
            for (int x : data) {
                out.write(x);
            }
            if (logging)
                Gdx.app.log("CONNECTION", "Sent message (int dump, length = " + data.length + ")");
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

    /**
     * Sets whether every single message sending / receiving is an event to be logged.
     */
    public void setDetailedLogging(boolean logging) {
        if (logging != this.logging) {
            Gdx.app.log("CONNECTION", "Detailed logging turned " + (logging ? "ON" : "OFF"));
            this.logging = logging;
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void dispose() {
        abortConnection();
    }
}
