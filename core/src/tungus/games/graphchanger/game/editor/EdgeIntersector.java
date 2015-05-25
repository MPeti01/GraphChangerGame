package tungus.games.graphchanger.game.editor;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.graph.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * Checks for {@link Edge Edges} and {@link Node Nodes} intersecting/on a given line segment.
 */
class EdgeIntersector {
    private static final Vector2 intersection = new Vector2();
    private List<Edge> edges = null;
    private List<Node> nodes = null;
    private final List<Edge> intersecting = new LinkedList<Edge>();
    private boolean hasIntersectingNode;


    void setLists(List<Edge> edges, List<Node> nodes) {
        this.edges = edges; this.nodes = nodes;
    }

    void updateFor(Vector2 start, Vector2 end) {
        intersecting.clear();
        hasIntersectingNode = false;
        for (Edge edge : edges) {
            if (Intersector.intersectSegments(start, end, edge.v1, edge.v2, intersection)) {
                if (intersection.dst2(end) > 1 && intersection.dst2(start) > 1) {
                    intersecting.add(edge);
                }
            }
        }
        for (Node node : nodes) {
            if (Intersector.distanceSegmentPoint(start, end, node.pos()) <= Node.RADIUS
                    && node.pos().dst2(start) > 1 && node.pos().dst2(end) > 1) {
                hasIntersectingNode = true;
                break;
            }
        }
    }

    int cutCount() {
        return intersecting.size();
    }

    Edge cutEdge() {
        if (intersecting.size() != 1)
            throw new IllegalStateException("Getting cut edge when there are " + intersecting.size());
        return intersecting.get(0);
    }

    boolean hasIntersectingNode() {
        return hasIntersectingNode;
    }

}
