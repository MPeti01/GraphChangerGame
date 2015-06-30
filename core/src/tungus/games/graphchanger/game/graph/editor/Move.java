package tungus.games.graphchanger.game.graph.editor;

import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.game.graph.Graph;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents an action a player can take.
 */

public abstract class Move {
    public abstract void applyTo(Graph graph);
    public abstract void write(OutputStream out);
    public static Move read(InputStream in) {
        try {
            int type = in.read();
            switch(type) {
                case AddEdgeMove.TYPE_ID:
                    return new AddEdgeMove(in.read(), in.read());
                case RemoveEdgeMove.TYPE_ID:
                    return new RemoveEdgeMove(in.read(), in.read());
                case UpgradeNodeMove.TYPE_ID:
                    return new UpgradeNodeMove(in.read());
                case 0:
                    return NULL; // No Move made
                default:
                    throw new GdxRuntimeException("Unknown Move type code " + type);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new GdxRuntimeException("Failed to read Move");
        }
    }

    /**
     * Used to represent no Move made in a tick in communication
     * (Technical reason: Queue doesn't support actual null elements...)
     */
    public static Move NULL = new Move() {
        @Override
        public void applyTo(Graph graph) {}
        @Override
        public void write(OutputStream out) {
            try {
                out.write(0);
            } catch (IOException e) {
                e.printStackTrace();
                throw new GdxRuntimeException("Couldn't write Move!");
            }
        }
    };
}
