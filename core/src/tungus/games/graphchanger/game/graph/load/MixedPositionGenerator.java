package tungus.games.graphchanger.game.graph.load;

import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.players.Player;

import java.util.Random;

/**
 * Generates pairs of symmetrical Nodes. All Nodes are owned by the players at the start,
 * each player owning one Node of all pairs.
 */
public class MixedPositionGenerator extends SymmetricGenerator {

    private static final int DEFAULT_NODE_COUNT = 30;

    private final int nodeCount;

    public MixedPositionGenerator(int seed, int nodeCount) {
        super(new Random(seed));
        this.nodeCount = nodeCount;
    }

    public MixedPositionGenerator(int seed) {
        this(seed, DEFAULT_NODE_COUNT);
    }

    @Override
    public void load() {
        while (nodes.size() < nodeCount) {
            Vector2 pos1 = new Vector2();
            Vector2 pos2 = new Vector2();
            positionPair(pos1, pos2);
            newNode(pos1, Player.P1);
            newNode(pos2, Player.P2);
        }
    }
}
