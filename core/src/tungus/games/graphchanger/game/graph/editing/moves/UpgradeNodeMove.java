package tungus.games.graphchanger.game.graph.editing.moves;

import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.game.graph.Graph;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UpgradeNodeMove extends Move {
    public static final int TYPE_ID = 3;
    private final int node;

    public UpgradeNodeMove(int node) {
        this.node = node;
    }

    public UpgradeNodeMove(InputStream in) throws IOException {
        this(in.read());
    }

    @Override
    public void applyTo(Graph graph) {
        graph.nodes.get(node).upgrade();
    }

    @Override
    public void write(OutputStream out) {
        try {
            out.write(TYPE_ID);
            out.write(node);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GdxRuntimeException("Failed to write Move");
        }

    }
}
