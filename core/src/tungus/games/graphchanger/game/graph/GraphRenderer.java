package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.Assets;
import tungus.games.graphchanger.DrawUtils;
import tungus.games.graphchanger.game.editor.GraphEditor;

import java.util.List;

/**
 * Renders the Nodes and Edges in a Graph.
 */
class GraphRenderer {

    public void drawEdges(List<Edge> edges, GraphEditor editor, SpriteBatch batch) {
        for (Edge edge : edges) {
            if (editor.isBeingCut(edge)) {
                batch.setColor(1, 0.8f, 0.8f, 0.8f);
            }
            DrawUtils.drawLine(batch, edge.v1, 10f, edge.length, edge.angle);
            batch.setColor(1, 1, 1, 1);
        }
    }

    public void drawNodes(List<Node> nodes, GraphEditor editor, SpriteBatch batch) {
        for (Node node : nodes) {
            Assets.Tex tex = (node.player() == null ? Assets.Tex.NODE0 : Assets.Tex.NODES[node.player().ordinal()]);
            if (editor.isSelected(node)) {
                tex = Assets.Tex.NODE_SELECTED;
            }
            batch.draw(tex.t, node.pos().x - Node.RADIUS, node.pos().y - Node.RADIUS, 2*Node.RADIUS, 2*Node.RADIUS);
        }
    }
}
