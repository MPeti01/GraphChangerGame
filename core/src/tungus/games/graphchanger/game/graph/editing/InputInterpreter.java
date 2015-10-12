package tungus.games.graphchanger.game.graph.editing;

import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.gamestate.GameState;
import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.graph.EdgePricer;
import tungus.games.graphchanger.game.graph.PartialEdge;
import tungus.games.graphchanger.game.graph.editing.moves.AddEdgeMove;
import tungus.games.graphchanger.game.graph.editing.moves.MoveListener;
import tungus.games.graphchanger.game.graph.editing.moves.RemoveEdgeMove;
import tungus.games.graphchanger.game.graph.editing.moves.UpgradeNodeMove;
import tungus.games.graphchanger.game.graph.node.Node;
import tungus.games.graphchanger.game.players.Player;
import tungus.games.graphchanger.input.BasicTouchListener;

import java.util.List;

/**
 * Takes touch events and calculates the moves the player is making.
 * Notifies GraphEditingUI when asked so that it can draw these.
 * Sends the completed valid Moves to a MoveListener.
 */
public class InputInterpreter implements BasicTouchListener {

    private static final float EDGE_START_SNAP_RADIUS = 35f;
    private static final float EDGE_END_SNAP_RADIUS = 50f;

    public static enum EditingState {IDLE, ADD, REMOVE}
    private EditingState state = EditingState.IDLE;
    private final MoveListener moveListener;

    private final Player moveMaker;
    private final NodeFinder nodeFinder = new NodeFinder();

    private final EdgeFinder edgeFinder = new EdgeFinder();
    private final MoveValidator validator;
    private EdgePricer pricer = null;

    private int startNodeID = -1;

    private int endNodeID = -1;
    private final Vector2 touchStart = new Vector2();
    private final Vector2 touchEnd = new Vector2();

    private boolean inputBlocked = true;

    public InputInterpreter(MoveListener l, Player moveMaker) {
        this.moveListener = l;
        this.moveMaker = moveMaker;

        validator = new MoveValidator(nodeFinder, edgeFinder, moveMaker);
    }

    /**
     * Enables or disables the editor accepting user input
     *
     * @param inp true to enable, false to disable
     */
    public void takeUserInput(boolean inp) {
        inputBlocked = !inp;
    }

    @Override
    public void onDown(Vector2 touch) {
        if (inputBlocked) return;
        touchStart.set(touch);
        touchEnd.set(touch);
        Node start = nodeFinder.nodeAt(touch, EDGE_START_SNAP_RADIUS);
        if (start != null && validator.canStartEdgeFrom(start)) {
            startNodeID = start.id;
            state = EditingState.ADD;
            touchStart.set(start.pos()); // Snap to center
        }
        else {
            state = EditingState.REMOVE;
        }
    }

    @Override
    public void onDrag(Vector2 touch) {
        if (inputBlocked) return;
        touchEnd.set(touch);
        if (state == EditingState.ADD) {
            // Check for end node to snap to
            Node end = nodeFinder.nodeAt(touch, EDGE_END_SNAP_RADIUS);
            if (end != null) {
                endNodeID = end.id;
                touchEnd.set(end.pos());
            } else {
                endNodeID = -1;
            }
        }
    }

    @Override
    public void onUp(Vector2 touch) {
        if (inputBlocked) return;
        if (state == EditingState.ADD
                && endNodeID != -1
                && validator.canConnect(startNodeID, endNodeID)) {
            moveListener.addMove(new AddEdgeMove(startNodeID, endNodeID));
        }
        else if (state == EditingState.REMOVE) {
            List<Edge> edgesToCut = edgeFinder.edgesThrough(touchStart, touchEnd, moveMaker);
            List<PartialEdge> partialToCut = edgeFinder.partialEdgesThrough(touchStart, touchEnd, moveMaker);
            if (!(edgesToCut.isEmpty() && partialToCut.isEmpty()) && validator.canCut(edgesToCut, partialToCut)) {
                moveListener.addMove(new RemoveEdgeMove(edgesToCut, partialToCut));
            }
        }

        state = EditingState.IDLE;
        startNodeID = endNodeID = -1;
    }

    @Override
    public void doubleTap(Vector2 touch) {
        if (inputBlocked) return;
        Node node = nodeFinder.nodeAt(touch, EDGE_START_SNAP_RADIUS);
        if (node != null && validator.canUpgrade(node)) {
            moveListener.addMove(new UpgradeNodeMove(node.id));
        }
    }

    public void updateUI(GraphEditingUI ui) {
        switch(state) {
            case IDLE:
                ui.noMove();
                break;
            case ADD:

                boolean valid = endNodeID != -1 && validator.canConnect(startNodeID, endNodeID);
                ui.connect(startNodeID, endNodeID, touchStart, touchEnd, valid);
                if (valid) {
                    String priceText = pricer.priceMultiplier(moveMaker)
                            + "*" + pricer.lengthPrice(touchStart, touchEnd);
                    ui.setConnectPrice(priceText);
                }

                break;
            case REMOVE:
                List<Edge> fullToCut = edgeFinder.edgesThrough(touchStart, touchEnd, moveMaker);
                List<PartialEdge> partialToCut = edgeFinder.partialEdgesThrough(touchStart, touchEnd, moveMaker);
                ui.cut(fullToCut, partialToCut, validator.canCut(fullToCut, partialToCut), touchStart, touchEnd);
                break;
        }
    }

    public void setGameState(GameState state) {
        edgeFinder.setEdges(state.graph.edges, state.graph.partialEdges);
        nodeFinder.setNodes(state.graph.nodes);
        pricer = state.edgePricer;
    }
}
