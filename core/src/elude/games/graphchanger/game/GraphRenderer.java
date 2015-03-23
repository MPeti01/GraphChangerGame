package elude.games.graphchanger.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import elude.games.graphchanger.Assets;
import elude.games.graphchanger.DrawUtils;

import java.util.List;

/**
 * Created by Peti on 2015.03.19..
 */
public class GraphRenderer {

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
            Assets.Tex tex = (node.player() == null ? Assets.Tex.NODE0 : Assets.Tex.NODE1);
            if (editor.isSelected(node)) {
                tex = Assets.Tex.NODE_SELECTED;
            }
            batch.draw(tex.t, node.pos().x - Node.RADIUS, node.pos().y - Node.RADIUS, 2*Node.RADIUS, 2*Node.RADIUS);
        }
    }
}
