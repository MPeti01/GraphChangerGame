package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.game.graph.editing.GraphEditingUI;
import tungus.games.graphchanger.game.graph.node.Node;

import java.util.List;

/**
 * Renders the {@link tungus.games.graphchanger.game.graph.node.Node Nodes} and {@link tungus.games.graphchanger.game.graph.Edge Edges} in a Graph.
 */
public class GraphRenderer {

    public void renderEdges(List<Edge> edges, List<PartialEdge> partialEdges, GraphEditingUI editor,
                            SpriteBatch batch, float delta) {
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        for (PartialEdge edge : partialEdges) {
            edge.renderBack(batch);
            batch.setColor(1, 1, 1, 1);
        }
        for (Edge edge : edges) {
            if (editor.isBeingCut(edge)) {
                batch.setColor(1, 1, 1, 0.5f);
            }
            edge.render(batch, delta);
            batch.setColor(1, 1, 1, 1);
        }
        for (PartialEdge edge : partialEdges) {
            if (editor.isBeingCut(edge)) {
                batch.setColor(1, 1, 1, 0.5f);
            }
            edge.renderFront(batch, delta);
            batch.setColor(1, 1, 1, 1);
        }
    }

    public void renderNodes(List<Node> nodes, GraphEditingUI editor, SpriteBatch batch, float delta) {
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        for (Node node : nodes) {
            node.render(batch, editor.isSelected(node), delta);
        }
    }
}
