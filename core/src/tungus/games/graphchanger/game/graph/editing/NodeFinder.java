package tungus.games.graphchanger.game.graph.editing;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.graph.node.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * Finds nodes in specific positions (or with a given ID).
 */
class NodeFinder {
    private List<Node> nodes = null;
    private final List<Node> intersecting = new LinkedList<Node>();

    public List<Node> nodesThrough(Vector2 start, Vector2 end) {
        intersecting.clear();
        for (Node node : nodes) {
            if (Intersector.distanceSegmentPoint(start, end, node.pos()) <= Node.RADIUS
                    && node.pos().dst2(start) > 1 && node.pos().dst2(end) > 1) {
                intersecting.add(node);
            }
        }
        return intersecting;
    }

    /**
     * Gets the Node closest to a given position and at most the given distance from it. <br/>
     * Returns null if no Node exists in the radius.
     */
    public Node nodeAt(Vector2 pos, float r) {
        float min = r*r;
        Node closest = null;
        for (Node n : nodes) {
            float d2 = pos.dst2(n.pos());
            if (d2 < min) {
                min = d2;
                closest = n;
            }
        }
        return closest;
    }

    public Node withID(int id) {
        return nodes.get(id);
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
