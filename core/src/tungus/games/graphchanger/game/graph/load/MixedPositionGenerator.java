package tungus.games.graphchanger.game.graph.load;

import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.players.Player;

import java.util.Random;

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
        float splitRatio = 0.1f + rand.nextFloat() * 0.2f;
        newNode(new Vector2(MARGIN + splitRatio * WIDTH, MARGIN + splitRatio * HEIGHT), Player.P1);
        newNode(new Vector2(MARGIN + (1 - splitRatio) * WIDTH, MARGIN + (1 - splitRatio) * HEIGHT), Player.P2);
        while (nodes.size() < nodeCount) {
            Vector2 pos1 = new Vector2();
            Vector2 pos2 = new Vector2();
            positionPair(pos1, pos2);
            newNode(pos1, Player.P1);
            newNode(pos2, Player.P2);
        }
    }
}
