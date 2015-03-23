package elude.games.graphchanger.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import elude.games.graphchanger.BasicTouchListener;
import elude.games.graphchanger.DrawUtils;

import java.util.List;

/**
 * Created by Peti on 2015.03.19..
 */
public class GraphEditor {

    private static enum EditingState {IDLE, ADD, REMOVE}
    private EditingState state = EditingState.IDLE;

    private final List<Node> nodes;
    private final List<Edge> edges;
    private final EdgeIntersector intersector;

    private Node startNode = null;
    private Node endNode = null;
    private Vector2 touchStart = new Vector2();
    private Vector2 touchEnd = new Vector2();
    private List<Edge> edgesBeingCut;

    final BasicTouchListener input = new BasicTouchListener() {
        @Override
        public void onDown(Vector2 touch) {
            touchStart.set(touch);
            state = EditingState.REMOVE;
            for (Node n : nodes) {
                if (touch.dst2(n.pos()) < Node.RADIUS * Node.RADIUS) {
                    startNode = n;
                    state = EditingState.ADD;
                    touchStart.set(startNode.pos());
                    break;
                }
            }
            touchEnd.set(touchStart);
        }

        @Override
        public void onDrag(Vector2 touch) {
            touchEnd.set(touch);
            if (state == EditingState.ADD) { // Snap to end node
                for (Node end : nodes) {
                    if (touch.dst2(end.pos()) < Node.RADIUS * Node.RADIUS) {
                        touchEnd.set(end.pos());
                        endNode = end;
                        edgesBeingCut = intersector.edgesCut(touchStart, touchEnd);
                        return;
                    }
                }
                endNode = null;
            }
            edgesBeingCut = intersector.edgesCut(touchStart, touchEnd);
        }

        @Override
        public void onUp(Vector2 touch) {
            touchEnd.set(touch);
            if (state == EditingState.REMOVE) {
                List<Edge> cut = intersector.edgesCut(touchStart, touchEnd);
                if (cut.size() == 1) {
                    Edge edge = cut.get(0);
                    edge.node1.removeNeighbor(edge.node2);
                    edge.node2.removeNeighbor(edge.node1);
                    edges.remove(edge);
                }
            } else if (state == EditingState.ADD) {
                if (endNode != null && intersector.edgesCut(touchStart, endNode.pos()).size() == 0 &&
                        !startNode.hasNeighbor(endNode) && startNode != endNode) {
                    startNode.addNeighbor(endNode);
                    endNode.addNeighbor(startNode);
                    edges.add(new Edge(startNode, endNode));
                }
                startNode = null;
                endNode = null;
            }
            state = EditingState.IDLE;
        }
    };

    public GraphEditor(List<Edge> edges, List<Node> nodes) {
        this.nodes = nodes;
        this.edges = edges;
        intersector = new EdgeIntersector(edges);
        edgesBeingCut = intersector.edgesCut(touchStart, touchEnd);
    }

    boolean isSelected(Node node) {
        return node == startNode || node == endNode;
    }

    boolean isBeingCut(Edge edge) {
        if (edgesBeingCut == null) {
            return false;
        }
        return state == EditingState.REMOVE && edgesBeingCut.contains(edge);
    }

    public void render(SpriteBatch batch) {
        if (state == EditingState.ADD) {
            if (edgesBeingCut.size() == 0) {
                batch.setColor(1, 1, 1, 0.5f);
            } else {
                batch.setColor(1, 0.5f, 0.5f, 0.5f);
            }
            DrawUtils.drawLine(batch, touchStart, touchEnd, 10f);
        } else if (state == EditingState.REMOVE) {
            if (edgesBeingCut.size() <= 1) {
                batch.setColor(1, 0.2f, 0.2f, 1f);
            } else {
                batch.setColor(0.7f, 0.2f, 0.2f, 0.65f);
            }
            DrawUtils.drawLine(batch, touchStart, touchEnd, 5f);
        }
        batch.setColor(1, 1, 1, 1);
    }
}
