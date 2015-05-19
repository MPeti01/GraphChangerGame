package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.BasicTouchListener;
import tungus.games.graphchanger.DrawUtils;

import java.util.List;

/**
 * The interface for adding edges to the graph or removing them.
 * Checks legality using
 */
class GraphEditor {

    private static enum EditingState {IDLE, ADD, REMOVE}
    private EditingState state = EditingState.IDLE;

    private final List<Node> nodes;
    private final List<Edge> edges;
    private final EdgeIntersector cutChecker;

    private Node startNode = null;
    private Node endNode = null;
    private final Vector2 touchStart = new Vector2();
    private final Vector2 touchEnd = new Vector2();
    private final MoveValidator moveValidator;

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
                        cutChecker.updateFor(touchStart, touchEnd);
                        return;
                    }
                }
                endNode = null;
            }
            cutChecker.updateFor(touchStart, touchEnd);
        }

        @Override
        public void onUp(Vector2 touch) {
            touchEnd.set(touch);
            if (state == EditingState.REMOVE) {
                cutChecker.updateFor(touchStart, touchEnd);
                if (cutChecker.cutCount() == 1 && moveValidator.canCut(cutChecker.cutEdge())) {
                    Edge edge = cutChecker.cutEdge();
                    edge.node1.removeNeighbor(edge.node2);
                    edge.node2.removeNeighbor(edge.node1);
                    edges.remove(edge);
                }
            } else if (state == EditingState.ADD) {
                if (endNode != null && cutChecker.cutCount() == 0
                        && !startNode.hasNeighbor(endNode) && startNode != endNode
                        && moveValidator.canConnect(startNode, endNode)) {
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

    public GraphEditor(List<Edge> edges, List<Node> nodes, MoveValidator validator) {
        this.nodes = nodes;
        this.edges = edges;
        this.moveValidator = validator;
        cutChecker = new EdgeIntersector(edges);
        cutChecker.updateFor(touchStart, touchEnd);
    }

    boolean isSelected(Node node) {
        return node == startNode || node == endNode;
    }

    boolean isBeingCut(Edge edge) {
        return state == EditingState.REMOVE
                && cutChecker.cutCount() == 1
                && cutChecker.cutEdge() == edge;
    }

    public void render(SpriteBatch batch) {
        if (state == EditingState.ADD) {
            if (cutChecker.cutCount() == 0) {
                batch.setColor(1, 1, 1, 0.5f);
            } else {
                batch.setColor(1, 0.5f, 0.5f, 0.5f);
            }
            DrawUtils.drawLine(batch, touchStart, touchEnd, 10f);
        } else if (state == EditingState.REMOVE) {
            if (cutChecker.cutCount() <= 1) {
                batch.setColor(1, 0.2f, 0.2f, 1f);
            } else {
                batch.setColor(0.7f, 0.2f, 0.2f, 0.65f);
            }
            DrawUtils.drawLine(batch, touchStart, touchEnd, 5f);
        }
        batch.setColor(1, 1, 1, 1);
    }
}
