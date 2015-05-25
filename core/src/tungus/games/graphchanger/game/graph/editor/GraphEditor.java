package tungus.games.graphchanger.game.graph.editor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.BasicTouchListener;
import tungus.games.graphchanger.DrawUtils;
import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.graph.Node;
import tungus.games.graphchanger.game.players.Player;

import java.util.List;

/**
 * Handles the UI for adding edges to the {@link Graph} or removing them.
 * Checks legality using MoveValidator and EdgeIntersector.
 */
public class GraphEditor {

    private static enum EditingState {IDLE, ADD, REMOVE}
    private EditingState state = EditingState.IDLE;

    private final List<Node> nodes;
    private final EdgeIntersector cutChecker;

    private int startNodeID = -1;
    private int endNodeID = -1;
    private final Vector2 touchStart = new Vector2();
    private final Vector2 touchEnd = new Vector2();
    private final MoveValidator moveValidator;

    private MoveListener moveListener = null;

    public final BasicTouchListener input = new BasicTouchListener() {

        @Override
        public void onDown(Vector2 touch) {
            touchStart.set(touch);
            state = EditingState.REMOVE;
            for (Node n : nodes) {
                if (touch.dst2(n.pos()) < Node.RADIUS * Node.RADIUS) {
                    startNodeID = n.id;
                    state = EditingState.ADD;
                    touchStart.set(n.pos());
                    break;
                }
            }
            touchEnd.set(touchStart);
        }

        @Override
        public void onDrag(Vector2 touch) {
            touchEnd.set(touch);
            if (state == EditingState.ADD) { // Snap to end node
                for (Node potentialEndNode : nodes) {
                    if (touch.dst2(potentialEndNode.pos()) < Node.RADIUS * Node.RADIUS) {
                        touchEnd.set(potentialEndNode.pos());
                        endNodeID = potentialEndNode.id;
                        cutChecker.updateFor(touchStart, touchEnd);
                        return;
                    }
                }
                endNodeID = -1;
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
                    moveListener.addMove(new Move(edge.node1.id, edge.node2.id, false));
                }
            } else if (state == EditingState.ADD) {
                if (endNodeID != -1) {
                    Node startNode = nodes.get(startNodeID);
                    Node endNode = nodes.get(endNodeID);
                    if (cutChecker.cutCount() == 0
                            && !cutChecker.hasIntersectingNode()
                            && !startNode.hasNeighbor(endNode) && startNode != endNode
                            && moveValidator.canConnect(startNode, endNode)) {
                        moveListener.addMove(new Move(startNodeID, endNodeID, true));
                    }
                }
                startNodeID = -1;
                endNodeID = -1;
            }
            state = EditingState.IDLE;
        }
    };

    public GraphEditor(Graph graph, Player p, MoveListener moveListener) {
        this.moveValidator = new MoveValidator(p);
        this.nodes = graph.nodes;
        this.moveListener = moveListener;
        cutChecker = new EdgeIntersector(graph.nodes, graph.edges);
        cutChecker.updateFor(touchStart, touchEnd);
    }

    public boolean isSelected(Node node) {
        return node.id == startNodeID || node.id == endNodeID;
    }

    public boolean isBeingCut(Edge edge) {
        return state == EditingState.REMOVE
                && cutChecker.cutCount() == 1
                && cutChecker.cutEdge() == edge;
    }

    public void render(SpriteBatch batch) {
        if (state == EditingState.ADD) {
            if (cutChecker.cutCount() == 0 && !cutChecker.hasIntersectingNode()) {
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

    public void set(GraphEditor other) {
        state = other.state;
        startNodeID = other.startNodeID;
        endNodeID = other.endNodeID;
        touchStart.set(other.touchStart);
        touchEnd.set(other.touchEnd);
        cutChecker.updateFor(touchStart, touchEnd);
    }
}
