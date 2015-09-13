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
    public Destination nextDestinationForArrived(Player owner);

    /**
     * Asks whether a unit's destination should be changed, if the unit has not yet reached this one.
     * @param owner The owner of the unit
     * @return this if no change is needed, null if the unit should be removed, a different Destination otherwise
     */
    public Destination remoteDestinationRedirect(Player owner);

    Destination localCopy(Graph graph);
}
