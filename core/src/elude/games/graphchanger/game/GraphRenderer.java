package elude.games.graphchanger.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import elude.games.graphchanger.Assets;

import java.util.List;

/**
 * Created by Peti on 2015.03.19..
 */
public class GraphRenderer {

    public void drawEdges(List<Edge> edges, GraphEditor editor, SpriteBatch batch) {
        for (Edge edge : edges) {
            batch.draw(Assets.Tex.EDGE.t, edge.v1.x, edge.v1.y, // Tex, pos
                       0, 0,                                    // Rotation point
                       edge.length, 10f,                         // Width, height
                       1, 1,                                    // Scale
                       edge.angle);                             // Rotation
        }
    }

    public void drawNodes(List<Node> nodes, GraphEditor editor, SpriteBatch batch) {
        for (Node node : nodes) {
            Assets.Tex tex = (node.player() == null ? Assets.Tex.NODE0 : Assets.Tex.NODE1);
            if (editor.isSelected(node)) {
                tex = Assets.Tex.NODE2;
            }
            batch.draw(tex.t, node.pos().x - Node.RADIUS, node.pos().y - Node.RADIUS, 2*Node.RADIUS, 2*Node.RADIUS);
        }
    }
}
