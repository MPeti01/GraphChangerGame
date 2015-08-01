package tungus.games.graphchanger.game.graph.editing;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.DrawUtils;
import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.graph.editing.InputInterpreter.EditingState;
import tungus.games.graphchanger.game.graph.node.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the UI for adding edges to the {@link Graph} or removing them.<br>
 * (Receives instructions from an InputInterpreter.)
 */
public class GraphEditingUI {

    private int startNodeID = -1, endNodeID = -1;
    private final List<Edge> edgesToCut = new ArrayList<Edge>();
    private Vector2 lineStart = new Vector2(), lineEnd = new Vector2();
    private EditingState state = EditingState.IDLE;
    private boolean isMoveValid = false;

    void noMove() {
        state = EditingState.IDLE;
    }

    void connect(int node1, int node2, Vector2 pos1, Vector2 pos2, boolean valid) {
        state = EditingState.ADD;
        startNodeID = node1;
        endNodeID = node2;
        isMoveValid = valid;
        lineStart.set(pos1);
        lineEnd.set(pos2);
    }

    void cut(List<Edge> edges, boolean valid, Vector2 touchStart, Vector2 touchEnd) {
        state = EditingState.REMOVE;
        edgesToCut.clear();
        edgesToCut.addAll(edges);
        lineStart.set(touchStart);
        lineEnd.set(touchEnd);
        isMoveValid = valid;
    }

    public boolean isSelected(Node node) {
        return state == EditingState.ADD
                && (node.id == startNodeID || node.id == endNodeID);
    }

    public boolean isBeingCut(Edge edge) {
        return state == EditingState.REMOVE
                && isMoveValid
                && edgesToCut.contains(edge);
    }

    public void render(SpriteBatch batch) {
        if (state == EditingState.ADD) {
            if (isMoveValid) {
                batch.setColor(1, 1, 1, 0.5f);
            } else {
                batch.setColor(1, 0.5f, 0.5f, 0.5f);
            }
            DrawUtils.drawLine(batch, lineStart, lineEnd, 10f);
        } else if (state == EditingState.REMOVE) {
            if (isMoveValid) {
                batch.setColor(1, 0.2f, 0.2f, 1f);
            } else {
                batch.setColor(0.7f, 0.2f, 0.2f, 0.65f);
            }
            DrawUtils.drawLine(batch, lineStart, lineEnd, 5f);
        }
        batch.setColor(1, 1, 1, 1);
    }
}
