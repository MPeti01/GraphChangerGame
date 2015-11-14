package tungus.games.graphchanger.game.graph.editing.moves;

import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.NetworkCommunicator;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents an action a player can take.
 */

public abstract class Move implements NetworkCommunicator.Writable {

    /**
     * Create a Move from the data stream received from a remote client. <br>
     * Note that it's written with the assumption that this WILL encode a Move!
     * (Some invalid Moves are handled gracefully but most are not.)
     *
     * @param m The bytes received, terminated by NetworkCommunicator.DELIM.
     *          Anything past that is invalid data.
     * @return The Move this message encoded or null if it wasn't a valid Move.
     */
    public static Move fromMessage(int[] m) {
        /* The first byte encodes the move's type, so the subclasses will always
        start reading the message from m[1]. */
        int type = m[0];
        switch (type) {
            case AddEdgeMove.TYPE_ID:
                return new AddEdgeMove(m);
            case RemoveEdgeMove.TYPE_ID:
                return new RemoveEdgeMove(m);
            case UpgradeNodeMove.TYPE_ID:
                return new UpgradeNodeMove(m);
            case 0:
                return NONE; // No Move made
            default:
                return null; // Not a valid Move
        }
    }

    public abstract void applyTo(Graph graph);

    /**
     * Used to represent no Move made in a tick in communication
     */
    public static final Move NONE = new Move() {
        @Override
        public void applyTo(Graph graph) {}
        @Override
        public void writeTo(OutputStream out) throws IOException {
            try {
                out.write(0);
            } catch (IOException e) {
                e.printStackTrace();
                throw new GdxRuntimeException("Couldn't write Move!");
            }
        }

        @Override
        public String toString() {
            return "No move";
        }
    };
}
