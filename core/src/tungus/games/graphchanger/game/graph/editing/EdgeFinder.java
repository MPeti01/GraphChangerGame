package tungus.games.graphchanger.game.graph.editing;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.graph.PartialEdge;
import tungus.games.graphchanger.game.graph.node.Node;
import tungus.games.graphchanger.game.players.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * Checks for {@link Edge Edges} intersecting a given line segment.
 */
class EdgeFinder {
    private static final Vector2 intersection = new Vector2();
    private List<Edge> edges = null;
    private List<PartialEdge> partialEdges = null;
    private final List<Edge> intersectingFull = new LinkedList<Edge>();
    private final List<PartialEdge> intersectingPartial = new LinkedList<PartialEdge>();

    public List<Edge> edgesThrough(Vector2 start, Vector2 end) {
        intersectingFull.clear();
        for (Edge edge : edges) {
            if (Intersector.intersectSegments(start, end, edge.v1, edge.v2, intersection)) {
                if (intersection.dst2(end) > 1 && intersection.dst2(start) > 1) {
                    intersectingFull.add(edge);
                }
            }
        }
        return intersectingFull;
    }

    /**
     * Only finds PartialEdges started by a given player. This way contested edges can be cut
     * without problems from trying to cut the other player's half as well.
     */
    public List<PartialEdge> partialEdgesThrough(Vector2 start, Vector2 end, Player player) {
        intersectingPartial.clear();
        for (PartialEdge edge : partialEdges) {
            if (edge.startNode().player() == player
                    && Intersector.intersectSegments(start, end, edge.startNode().pos(), edge.endNode().pos(), intersection)) {
                if (intersection.dst2(end) > 1 && intersection.dst2(start) > 1) {
                    intersectingPartial.add(edge);
                }
            }
        }
        return intersectingPartial;
    }

    public void setEdges(List<Edge> edges, List<PartialEdge> partialEdges) {
        this.edges = edges;
        this.partialEdges = partialEdges;
    }

    public boolean hasEdgeBetween(Node node1, Node node2) {
        for (Edge edge : edges) {
            if (edge.node1.equals(node1) && edge.node2.equals(node2) ||
                edge.node1.equals(node2) && edge.node2.equals(node1)) {
                return true;
            }
        }
        return false;
    }
}
