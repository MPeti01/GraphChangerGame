package tungus.games.graphchanger.game.graph.editing.moves;

import tungus.games.graphchanger.game.graph.Graph;

import java.io.IOException;
import java.io.OutputStream;

public class AddEdgeMove extends Move {

    public static final int TYPE_ID = 1;
    private final int node1, node2;

    public AddEdgeMove(int node1, int node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    public AddEdgeMove(int[] m) {
        this(m[1], m[2]);
    }

    @Override
    public void applyTo(Graph graph) {
        graph.addEdge(node1, node2);
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(TYPE_ID);
        out.write(node1);
        out.write(node2);
    }

    @Override
    public String toString() {
        return "Connect " + node1 + " and " + node2;
    }
}
