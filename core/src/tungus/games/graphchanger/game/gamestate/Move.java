package tungus.games.graphchanger.game.gamestate;

/**
 * Created by Peti on 2015.05.21..
 */
public class Move {
    public final int node1ID, node2ID;
    public final boolean add;
    public Move(int n1, int n2, boolean a) {
        node1ID = n1; node2ID = n2; add = a;
    }
}
