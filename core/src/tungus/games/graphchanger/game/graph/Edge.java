package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.DrawUtils;
import tungus.games.graphchanger.game.graph.node.Node;
import tungus.games.graphchanger.game.players.Player;

/**
 * An edge in the graph, connecting two {@link tungus.games.graphchanger.game.graph.node.Node Nodes}.
 */
public class Edge implements Destination {

    private static final float COLLIDER_RADIUS = 10f;
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
        Color c = node1.player().edgeColor;
        batch.setColor(c.r, c.g, c.b, batch.getColor().a);
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

    @Override
    public Vector2 pos() {
        return v2;
    }

    @Override
    public boolean isReachedAt(Vector2 unitPos) {
        return unitPos.dst2(v2) < COLLIDER_RADIUS * COLLIDER_RADIUS;
    }

    @Override
    public Destination nextDestinationFor(Player owner) {
        return node2;
    }

    @Override
    public Destination localCopy(Graph g) {
        for (Edge e : g.edges) {
            if (e.equals(this)) {
                return e;
            }
        }
        // No Edge in that list. Either it was reduced to a partial or removed since last frame.
        for (PartialEdge e : g.partialEdges) {
            if (e.startNode().equals(node1) && e.endNode().equals(node2)) {
                return e;
            }
        }
        // No PartialEdge found, the Edge must have been removed
        return null;
    }
}
