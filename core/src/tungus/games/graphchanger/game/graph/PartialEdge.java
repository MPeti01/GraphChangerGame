package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.Assets.Tex;
import tungus.games.graphchanger.drawutils.DrawUtils;
import tungus.games.graphchanger.game.graph.node.Node;
import tungus.games.graphchanger.game.players.Player;
import tungus.games.graphchanger.game.render.EdgeEffect;

/**
 * An Edge under construction by an EdgeBuilder.
 */
public class PartialEdge implements Destination, Comparable<PartialEdge> {

    public static interface EdgeCompleteListener {
        public void onEdgeComplete(PartialEdge built);
    }

    private static final Vector2 temp = new Vector2();

    public final int totalCost;

    private final float progressStep;
    private final Node start, end;
    private float progress; // 0 to 1

    private Vector2 front = new Vector2();

    private final float angle;
    private final float fullLength;
    private boolean cut = false;
    private Edge finishedEdge = null;
    private final EdgeCompleteListener onCompleteListener;

    private EdgeEffect effect;

    public PartialEdge(Node start, Node end, int cost, float progress, EdgeCompleteListener listener) {
        this.progress = progress;
        totalCost = cost;
        progressStep = 1f / cost;
        this.start = start;
        this.end = end;
        onCompleteListener = listener;
        angle = temp.set(end.pos()).sub(start.pos()).angle();
        fullLength = start.pos().dst(end.pos());
        effect = new EdgeEffect(start.player(), start.pos(), angle, fullLength, 0f);
        updateFront();
    }

    private void updateFront() {
        // Linearly interpolate from start to end by progress
        front.set(end.pos()).sub(start.pos()).scl(progress).add(start.pos());
        effect.setProgress(progress);
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

    public EdgeEffect getEffect() {
        return effect;
    }

    @Override
    public boolean isReachedAt(Vector2 unitPos) {
        // Past front, i.e. in the same direction from front that end is from start
        return front.equals(unitPos) ||
                ((start.pos().x != end.pos().x) ?
                        (start.pos().x < end.pos().x) == (front.x < unitPos.x) :
                        (start.pos().y < end.pos().y) == (front.y < unitPos.y));
    }

    @Override
    public Destination nextDestinationForArrived(Player owner) {
        if (progress == 1) {
            return end;
        } else {
            unitArrived();
            return null;
        }
    }

    @Override
    public Destination remoteDestinationRedirect(Player owner) {
        if (cut)
            return null;
        else if (finishedEdge != null)
            return finishedEdge.remoteDestinationRedirect(owner);
        else
            return this;
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
                return e;
            }
        }
        // No finished Edge found, it must have been removed.
        return null;
    }

    public float progress() {
        return progress;
    }

    public void unitArrived() {
        progress += progressStep;
        if (progress > 0.9999f) {
            progress = 1;
            onCompleteListener.onEdgeComplete(this);
        }
        end.reachingWithEdge(start, progress);
        updateFront();
    }

    /**
     * Sets the progress to the given parameter if it was higher.
     */
    public void boundProgress(float maxProgress) {
        progress = Math.min(progress, maxProgress);
        updateFront();
    }

    public void finishAs(Edge finished) {
        finishedEdge = finished;
    }

    public void cut() {
        cut = true;
    }

    public void renderBack(SpriteBatch batch) {
        batch.setColor(start.player().backColor);
        DrawUtils.drawLine(batch, Tex.SPOT.t, start.pos(), 20f, fullLength, angle);
    }

    public void renderFront(SpriteBatch batch, float delta) {
        effect.draw(batch, delta);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PartialEdge)) return false;
        PartialEdge other = (PartialEdge) o;
        return start.equals(other.start) && end.equals(other.end);
    }

    @Override
    public int compareTo(PartialEdge other) {
        if (other.end.equals(this.end))
            return other.start.id - this.start.id;
        else
            return other.end.id - this.end.id;
    }

    public void set(PartialEdge other) {
        progress = other.progress;
        updateFront();
    }
}
