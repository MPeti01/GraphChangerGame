package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.graph.node.Node;
import tungus.games.graphchanger.game.players.Player;

/**
 * Handles the pricing of building Edges: Stores relevant information (number of edges built by players)
 * and calculates the resulting price components or total prices. <br>
 * There are two parts of an Edge's price. It depends on the <b>length of the edge</b> and
 * <b>the number of edges built by that player</b>.
 */
public class EdgePricer {

    private static final float LENGTH_PER_UNIT_COST = 140f;
    private static final float EDGES_PER_MULTIPLIER_INCREASE = 4f;

    private int[] edgesByPlayers = new int[Player.values().length];

    public int lengthPrice(Vector2 start, Vector2 end) {
        return (int)(start.dst(end) / LENGTH_PER_UNIT_COST) + 1;
    }

    public int priceMultiplier(Player p) {
        return (int)(edgesByPlayers[p.ordinal()] / EDGES_PER_MULTIPLIER_INCREASE) + 1;
    }

    public int totalPrice(Node start, Node end) {
        return lengthPrice(start.pos(), end.pos()) * priceMultiplier(start.player());
    }

    public void edgeBuilt(Player p) {
        edgesByPlayers[p.ordinal()]++;
    }

    public void set(EdgePricer other) {
        System.arraycopy(other.edgesByPlayers, 0, edgesByPlayers, 0, edgesByPlayers.length);
    }
}
