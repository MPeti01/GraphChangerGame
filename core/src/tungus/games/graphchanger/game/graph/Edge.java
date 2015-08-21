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
    private static final Vector2 temp2 = new Vector2();
    public final Node node1;
    public final Node node2;

    public final Vector2 v1;
    public final Vector2 v2;
    public final float length;
    public final float angle;
    public final int totalCost;
    public int costLeft;

    public Edge(Node n1, Node n2, int totalCost, int costLeft) {
        node1 = n1;
        node2 = n2;
        v1 = n1.pos();
        v2 = n2.pos();
        angle = temp.set(v2).sub(v1).angle();
        length = v1.dst(v2);
        this.totalCost = totalCost;
        this.costLeft = costLeft;
    }

    public Edge(Node n1, Node n2, int price) {
        this(n1, n2, price, price);
    }

    public Edge(Edge other) {
        this(other.node1, other.node2, other.totalCost, other.costLeft);
    }

    public boolean isComplete() {
        return costLeft == 0;
    }

    public void unitArrived() {
        costLeft--;
    }

    public void render(SpriteBatch batch) {
        float progress = 1f - ((float)costLeft/ totalCost) * 0.9f;
        temp.set(v2).sub(v1).scl(progress).add(v1);
        DrawUtils.drawLine(batch, v1, temp, 10f);
        temp2.set(25f, 0).rotate(angle).add(v1).add(temp).scl(0.5f);
        DrawUtils.drawLine(batch, temp2, 10f, 25f, angle + 135f);
        DrawUtils.drawLine(batch, temp2, 10f, 25f, angle - 135f);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edge))
            return false;
        Edge other = (Edge)o;
        return (other.node1.equals(node1)) && (other.node2.equals(node2));
    }
}
