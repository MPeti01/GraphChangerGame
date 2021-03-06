package tungus.games.graphchanger.game.graph.editing;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.Assets;
import tungus.games.graphchanger.drawutils.DrawUtils;
import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.graph.PartialEdge;
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
    private final List<PartialEdge> partialEdgesToCut = new ArrayList<PartialEdge>();
    private Vector2 lineStart = new Vector2(), lineEnd = new Vector2();
    private EditingState state = EditingState.IDLE;
    private boolean isMoveValid = false;
    private String priceText;

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

    void cut(List<Edge> edges, List<PartialEdge> partialToCut, boolean valid, Vector2 touchStart, Vector2 touchEnd) {
        state = EditingState.REMOVE;
        edgesToCut.clear();
        edgesToCut.addAll(edges);
        partialEdgesToCut.clear();
        partialEdgesToCut.addAll(partialToCut);
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

    public boolean isBeingCut(PartialEdge partialEdge) {
        return state == EditingState.REMOVE
                && isMoveValid
                && partialEdgesToCut.contains(partialEdge);
    }

    public void renderBehindNodes(SpriteBatch batch) {
        if (state == EditingState.ADD) {
            if (isMoveValid) {
                batch.setColor(1, 1, 1, 0.5f);
            } else {
                batch.setColor(1, 0.5f, 0.5f, 0.5f);
            }
            DrawUtils.drawLine(batch, lineStart, lineEnd, 10f);
        }
        batch.setColor(1, 1, 1, 1);
    }

    public void renderOnTop(SpriteBatch batch) {
        if (state == EditingState.ADD && isMoveValid) {
            Assets.font.draw(batch, priceText,
                    (lineStart.x + lineEnd.x) / 2, (lineStart.y + lineEnd.y) / 2);
        }
        else if (state == EditingState.REMOVE) {
            if (isMoveValid) {
                batch.setColor(1, 0.2f, 0.2f, 1f);
            } else {
                batch.setColor(0.7f, 0.2f, 0.2f, 0.65f);
            }
            DrawUtils.drawLine(batch, lineStart, lineEnd, 5f);
        }
        batch.setColor(1, 1, 1, 1);
    }

    public void setConnectPrice(String text) {
        priceText = text;
    }
}
