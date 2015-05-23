package tungus.games.graphchanger.game.gamestate;

/**
 * Represents an action a player can take: Adding or removing an edge between two nodes.
 */
public class Move {
    public final int node1ID, node2ID;
    public final boolean add;
    public Move(int n1, int n2, boolean a) {
        node1ID = n1; node2ID = n2; add = a;
    }
}
