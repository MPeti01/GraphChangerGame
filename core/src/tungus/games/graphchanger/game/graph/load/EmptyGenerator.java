package tungus.games.graphchanger.game.graph.load;

import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.players.Player;

import java.util.Random;

/**
 * Generates a set of Nodes with central symmetry. Only one Node is owned by each player.
 */
public class EmptyGenerator extends SymmetricGenerator {

    private static final int DEFAULT_NODE_COUNT = 30;

    private final int nodeCount;

    public EmptyGenerator(int seed, int nodeCount) {
        super(new Random(seed));
        this.nodeCount = nodeCount;
    }

    public EmptyGenerator(int seed) {
        this(seed, DEFAULT_NODE_COUNT);
    }

    @Override
    public void load() {
        float splitRatio = 0.1f + rand.nextFloat() * 0.2f;
        newNode(new Vector2(MARGIN + splitRatio * WIDTH, MARGIN + splitRatio * HEIGHT), Player.P1, 0);
        newNode(new Vector2(MARGIN + (1 - splitRatio) * WIDTH, MARGIN + (1 - splitRatio) * HEIGHT), Player.P2, 0);
        while (nodes.size() < nodeCount) {
            Vector2 pos1 = new Vector2();
            Vector2 pos2 = new Vector2();
            positionPair(pos1, pos2);
            newNode(pos1);
            newNode(pos2);
        }
    }
}
