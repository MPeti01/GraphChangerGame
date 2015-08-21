package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.DrawUtils;
import tungus.games.graphchanger.game.graph.node.Node;
import tungus.games.graphchanger.game.players.Player;

/**
 * An Edge under construction by an EdgeBuilder.
 */
public class PartialEdge implements Destination {

    private static final float COLLIDER_RADIUS = 15f;
    private static final Vector2 temp = new Vector2();

    public final int totalCost;
    private final float progressStep;
    private final Node start, end;
    private float progress; // 0 to 1
    private Vector2 front = new Vector2();

    private final float angle;

    public PartialEdge(Node start, Node end, int cost, float progress) {
        this.progress = progress;
        totalCost = cost;
        progressStep = 1f / cost;
        this.start = start;
        this.end = end;
        angle = temp.set(end.pos()).sub(start.pos()).angle();
        updateFront();
    }

    private void updateFront() {
        // Linearly interpolate from start to end by progress
        front.set(end.pos()).sub(start.pos()).scl(progress).add(start.pos());
    }

    @Override
    public Vector2 pos() {
        return front;
    }

    public Node startNode() {
        return start;
    }

    public Node endNode() {
        return end;
    }

    public float progress() {
        return progress;
    }

    @Override
    public boolean isReachedAt(Vector2 unitPos) {
        return unitPos.dst2(front) < COLLIDER_RADIUS*COLLIDER_RADIUS;
        //TODO OR if the Unit is past that if the front jumped back! Rare bug possibility, but should fix
    }

    @Override
    public Destination nextDestinationFor(Player owner) {
        return progress == 1 ? end : null;
    }

    public void unitArrived() {
        progress += progressStep;
        updateFront();
    }

    public boolean isComplete() {
        return progress >= 0.9999f; // Fuzzy testing in case of float inaccuracy
    }

    /**
     * Sets the progress to the given parameter if it was higher.
     */
    public void boundProgress(float maxProgress) {
        progress = Math.min(progress, maxProgress);
        updateFront();
    }

    public void render(SpriteBatch batch) {
        // Draw the edge
        DrawUtils.drawLine(batch, start.pos(), front, 10f);

        // Draw the two short lines for the arrow
        temp.set(25f, 0).rotate(angle).add(start.pos()).add(front).scl(0.5f);
        DrawUtils.drawLine(batch, temp, 10f, 25f, angle + 135f);
        DrawUtils.drawLine(batch, temp, 10f, 25f, angle - 135f);
    }

    public void set(PartialEdge other) {
        progress = other.progress;
        updateFront();
    }

    @Override
    public Destination localCopy(Graph g) {
        for (PartialEdge e : g.partialEdges) {
            if (this.equals(e)) {
                return e;
            }
        }
        // No PartialEdge in the list. Either completed or canceled since last frame. Check if there's a finished Edge
        for (Edge e : g.edges) {
            if (e.node1.equals(start) && e.node2.equals(end)) {
                // The Edge was completed, anyone coming here should go on to its end
                return g.nodes.get(end.id);
            }
        }
        // No finished Edge found, it must have been removed.
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PartialEdge)) return false;
        PartialEdge other = (PartialEdge)o;
        return start.equals(other.start) && end.equals(other.end);
    }
}
