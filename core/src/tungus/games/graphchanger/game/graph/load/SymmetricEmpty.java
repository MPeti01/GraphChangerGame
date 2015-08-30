package tungus.games.graphchanger.game.graph.load;

import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.gamescreen.GameScreen;
import tungus.games.graphchanger.game.graph.node.Node;
import tungus.games.graphchanger.game.players.Player;

import java.util.Random;

/**
 * Generates a set of Nodes with central symmetry. Only one Node is owned by each player.
 */
public class SymmetricEmpty extends GraphLoader {

    private static final int DEFAULT_NODE_COUNT = 30;
    private static final float MARGIN = 30;
    private static final float WIDTH = GameScreen.GAME_WIDTH - 2 * MARGIN;
    private static final float HEIGHT = GameScreen.GAME_HEIGHT - 2 * MARGIN;
    private static final float MIN_DIST = 80;

    private final Random rand;
    private final int nodeCount;

    public SymmetricEmpty(int seed, int nodeCount) {
        this.rand = new Random(seed);
        this.nodeCount = nodeCount;
    }

    public SymmetricEmpty(int seed) {
        this(seed, DEFAULT_NODE_COUNT);
    }

    @Override
    public void load() {
        float splitRatio = 0.1f + rand.nextFloat() * 0.2f;
        newNode(new Vector2(MARGIN + splitRatio * WIDTH, MARGIN + splitRatio * HEIGHT), Player.P1);
        newNode(new Vector2(MARGIN + (1 - splitRatio) * WIDTH, MARGIN + (1 - splitRatio) * HEIGHT), Player.P2);
        Vector2 bottomLeft = new Vector2(MARGIN, MARGIN);
        Vector2 topRight = new Vector2(MARGIN + WIDTH, MARGIN + HEIGHT);
        while (nodes.size() < nodeCount) {
            Vector2 pos1 = new Vector2(rand.nextFloat() * WIDTH, rand.nextFloat() * HEIGHT);
            Vector2 pos2 = new Vector2(topRight).sub(pos1);
            pos1.add(bottomLeft);
            if (pos1.dst2(pos2) < MIN_DIST * MIN_DIST) {
                continue;
            }
            boolean allOK = true;
            for (Node n : nodes) {
                if (n.pos().dst2(pos1) < MIN_DIST * MIN_DIST || n.pos().dst2(pos2) < MIN_DIST * MIN_DIST) {
                    allOK = false;
                    break;
                }
            }
            if (!allOK)
                continue;
            newNode(pos1);
            newNode(pos2);
        }
    }
}
