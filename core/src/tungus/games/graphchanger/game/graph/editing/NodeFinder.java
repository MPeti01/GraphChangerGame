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

    public Node nodeAt(Vector2 pos) {
        for (Node n : nodes) {
            if (pos.dst2(n.pos()) < Node.RADIUS * Node.RADIUS) {
                return n;
            }
        }
        return null;
    }

    public Node withID(int id) {
        return nodes.get(id);
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
