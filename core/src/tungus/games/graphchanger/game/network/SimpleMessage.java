package tungus.games.graphchanger.game.network;

import tungus.games.graphchanger.game.network.NetworkCommunicator.Writable;

import java.io.IOException;
import java.io.OutputStream;

public class SimpleMessage implements Writable {
    private final int[] m;

    public SimpleMessage(int... message) {
        m = message;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        for (int x : m) {
            out.write(x);
        }
    }
}
