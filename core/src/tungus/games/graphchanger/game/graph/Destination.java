package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.players.Player;

/**
 * Interface used for Unit movement goals, implemented by Node and PartialEdge
 */
public interface Destination {
    /**
     * @return The position of the goal where the unit should go
     */
    public Vector2 pos();

    /**
     * Basically handles collision checking for the Destination: whether a unit at a given point isReachedAt it or not
     * @param unitPos The position of a unit
     * @return Whether or not it's isReachedAt the destination
     */
    public boolean isReachedAt(Vector2 unitPos);

    /**
     * Notfies the Destination that a unit passed it. Returns what should happen to it.
     * @param owner The owner of the unit
     * @return The next destination for the unit if this Destination cannot consume it, null if it can and did.
     */
    public Destination nextDestinationFor(Player owner);

    /**
     * Given a Graph instance from a different GameState frame, returns the Destination corresponding to itself.
     */
    public Destination localCopy(Graph g);
}
