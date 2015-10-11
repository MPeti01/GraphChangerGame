package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.graph.node.Node;
import tungus.games.graphchanger.game.players.Player;
import tungus.games.graphchanger.game.render.EdgeEffect;

/**
 * An edge in the graph, connecting two {@link tungus.games.graphchanger.game.graph.node.Node Nodes}.
 */
public class Edge implements Destination, Comparable<Edge> {

    private static final float COLLIDER_RADIUS = 10f;
    private static final Vector2 temp = new Vector2();
    public final Node node1;
    public final Node node2;

    public final Vector2 v1;
    public final Vector2 v2;
    public final float length;
    public final float angle;

    private final EdgeEffect effect;

    /**
     * Whether the Edge was removed (by the owner's cut or the opponent's countering Edge)
     */
    private boolean cut = false;
    /**
     * The PartialEdge this Edge became if it was contested by an opponent. null while the Edge still exists.
     */
    private PartialEdge partialReplacer = null;

    public Edge(Node n1, Node n2) {
        node1 = n1;
        node2 = n2;
        v1 = n1.pos();
        v2 = n2.pos();
        angle = temp.set(v2).sub(v1).angle();
        length = v1.dst(v2);
        effect = new EdgeEffect(node1.player(), v1, angle, length, 1f);
    }

    public void render(SpriteBatch batch, float delta) {
        effect.draw(batch, delta);
    }

    public void contestedAs(PartialEdge partial) {
        cut();
        partialReplacer = partial;
    }

    public void cut() {
        cut = true;
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
    public Destination nextDestinationForArrived(Player owner) {
        return node2;
    }

    @Override
    public Destination remoteDestinationRedirect(Player owner) {
        if (!cut) {
            return this;
        } else if (partialReplacer == null) {
            return null;
        } else {
            return partialReplacer.remoteDestinationRedirect(owner);
        }
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

    @Override
    public int compareTo(Edge other) {
        if (other.node2.equals(this.node2))
            return other.node1.id - this.node1.id;
        else
            return other.node2.id - this.node2.id;
    }
}
