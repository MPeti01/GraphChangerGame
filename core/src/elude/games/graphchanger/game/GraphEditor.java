package elude.games.graphchanger.game;

import com.badlogic.gdx.math.Vector2;
import elude.games.graphchanger.BasicTouchListener;

import java.util.List;

/**
 * Created by Peti on 2015.03.19..
 */
public class GraphEditor {

    private final List<Node> nodes;
    private final List<Edge> edges;
    private Node start = null;

    final BasicTouchListener input = new BasicTouchListener() {
        @Override
        public void onDown(Vector2 touch) {
            for (Node n : nodes) {
                if (touch.dst2(n.pos()) < Node.RADIUS * Node.RADIUS) {
                    start = n;
                    break;
                }
            }
        }

        @Override
        public void onDrag(Vector2 touch) {

        }

        @Override
        public void onUp(Vector2 touch) {
            if (start == null)
                return;
            for (Node end : nodes) {
                if (touch.dst2(end.pos()) < Node.RADIUS * Node.RADIUS) {
                    start.addNeighbor(end);
                    end.addNeighbor(start);
                    edges.add(new Edge(start, end));
                    break;
                }
            }
        }
    };

    public GraphEditor(List<Edge> edges, List<Node> nodes) {
        this.nodes = nodes;
        this.edges = edges;
    }

    boolean isSelected(Node node) {
        return node == start;
    }

    boolean isBeingCut(Edge edge) {
        return false;
    }
}
