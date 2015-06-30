package tungus.games.graphchanger.game.graph.editor;

import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.game.graph.Graph;

import java.io.IOException;
import java.io.OutputStream;

class RemoveEdgeMove extends Move {
    public static final int TYPE_ID = 2;
    private final int node1, node2;

    public RemoveEdgeMove(int node1, int node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    @Override
    public void applyTo(Graph graph) {
        graph.removeEdge(node1, node2);
    }

    @Override
    public void write(OutputStream out) {
        try {
            out.write(TYPE_ID);
            out.write(node1);
            out.write(node2);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GdxRuntimeException("Failed to write Move");
        }
    }

    @Override
    public String toString() {
        return "[Cut " + node1 + " and " + node2 + "]";
    }
}
