package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.DrawUtils;
import tungus.games.graphchanger.game.graph.node.Node;

/**
 * An edge in the graph, connecting two {@link tungus.games.graphchanger.game.graph.node.Node Nodes}. <br>
 * Nodes store their neighbors, so this class is for building and rendering.
 */
public class Edge {

    private static final Vector2 temp = new Vector2();
    public final Node node1;
    public final Node node2;

    public final Vector2 v1;
    public final Vector2 v2;
    public final float length;
    public final float angle;

    public Edge(Node n1, Node n2) {
        node1 = n1;
        node2 = n2;
        v1 = n1.pos();
        v2 = n2.pos();
        angle = temp.set(v2).sub(v1).angle();
        length = v1.dst(v2);
    }

    public void render(SpriteBatch batch) {
        DrawUtils.drawLine(batch, v1, v2, 10f);

        // Draw the two short lines for the arrow
        temp.set(25f, 0).rotate(angle).add(v1).add(v2).scl(0.5f);
        DrawUtils.drawLine(batch, temp, 10f, 25f, angle + 135f);
        DrawUtils.drawLine(batch, temp, 10f, 25f, angle - 135f);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edge))
            return false;
        Edge other = (Edge)o;
        return (other.node1.equals(node1)) && (other.node2.equals(node2));
    }
}
