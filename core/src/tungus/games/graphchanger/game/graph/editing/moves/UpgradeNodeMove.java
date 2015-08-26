package tungus.games.graphchanger.game.graph.editing.moves;

import tungus.games.graphchanger.game.graph.Graph;

import java.io.IOException;
import java.io.OutputStream;

public class UpgradeNodeMove extends Move {
    public static final int TYPE_ID = 3;
    private final int node;

    public UpgradeNodeMove(int node) {
        this.node = node;
    }

    public UpgradeNodeMove(int[] m) {
        this(m[1]);
    }

    @Override
    public void applyTo(Graph graph) {
        graph.nodes.get(node).upgrade();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(TYPE_ID);
        out.write(node);
    }

    @Override
    public String toString() {
        return "Upgrade " + node;
    }
}
