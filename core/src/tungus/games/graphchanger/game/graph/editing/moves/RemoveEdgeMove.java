package tungus.games.graphchanger.game.graph.editing.moves;

import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.graph.PartialEdge;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class RemoveEdgeMove extends Move {
    public static final int TYPE_ID = 2;
    private final int[] idList;

    public RemoveEdgeMove(List<Edge> fullEdges, List<PartialEdge> partialEdges) {
        idList = new int[fullEdges.size()*2 + partialEdges.size()*2];
        for (int i = 0; i < fullEdges.size(); i++) {
            Edge e = fullEdges.get(i);
            idList[2*i] = e.node1.id;
            idList[2*i+1] = e.node2.id;
        }
        int offset = fullEdges.size()*2;
        for (int i = 0; i < partialEdges.size(); i++) {
            PartialEdge e = partialEdges.get(i);
            idList[offset+2*i] = e.startNode().id;
            idList[offset+2*i+1] = e.endNode().id;
        }
    }

    public RemoveEdgeMove(InputStream in) throws IOException {
        idList = new int[in.read()];
        for (int i = 0; i < idList.length; i++) {
            idList[i] = in.read();
        }
    }

    @Override
    public void applyTo(Graph graph) {
        for (int i = 0; 2*i < idList.length; i++) {
            graph.removeEdge(idList[2 * i], idList[2 * i + 1]);
        }
    }

    @Override
    public void write(OutputStream out) {
        try {
            out.write(TYPE_ID);
            out.write(idList.length);
            for (int x : idList) {
                out.write(x);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new GdxRuntimeException("Failed to write Move");
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; 2*i < idList.length; i++) {
            builder.append("Cut " + idList[2 * i] + " and " + idList[2 * i + 1] + "; ");
        }
        return builder.toString();
    }
}
