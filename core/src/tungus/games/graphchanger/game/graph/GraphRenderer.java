package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.Assets;
import tungus.games.graphchanger.DrawUtils;
import tungus.games.graphchanger.game.graph.editor.GraphEditingUI;
import tungus.games.graphchanger.game.graph.node.Node;

import java.util.List;

/**
 * Renders the {@link tungus.games.graphchanger.game.graph.node.Node Nodes} and {@link tungus.games.graphchanger.game.graph.Edge Edges} in a Graph.
 */
public class GraphRenderer {

    public void render(Graph graph, GraphEditingUI editor, SpriteBatch batch) {
        drawEdges(graph.edges, editor, batch);
        editor.render(batch);
        drawNodes(graph.nodes, editor, batch);
    }

    public void drawEdges(List<Edge> edges, GraphEditingUI editor, SpriteBatch batch) {
        for (Edge edge : edges) {
            if (editor.isBeingCut(edge)) {
                batch.setColor(1, 0.8f, 0.8f, 0.8f);
            }
            DrawUtils.drawLine(batch, edge.v1, 10f, edge.length, edge.angle);
            batch.setColor(1, 1, 1, 1);
        }
    }

    public void drawNodes(List<Node> nodes, GraphEditingUI editor, SpriteBatch batch) {
        for (Node node : nodes) {
            Assets.Tex tex = (node.player() == null ? Assets.Tex.NODE0 : Assets.Tex.NODES[node.player().ordinal()]);
            if (editor.isSelected(node)) {
                tex = Assets.Tex.NODE_SELECTED;
            }
            batch.draw(tex.t, node.pos().x - Node.RADIUS, node.pos().y - Node.RADIUS, 2*Node.RADIUS, 2*Node.RADIUS);
            node.render(batch); //TODO sort this out properly..
        }
    }
}
