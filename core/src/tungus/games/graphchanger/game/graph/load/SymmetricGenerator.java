package tungus.games.graphchanger.game.graph.load;

import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.gamescreen.GameScreen;
import tungus.games.graphchanger.game.graph.node.Node;

import java.util.Random;

/**
 * A Graph generator for easily creating symmetrical pairs of Nodes at a
 * reasonable distance from each other.
 */
abstract class SymmetricGenerator extends GraphLoader {
    protected static final float MARGIN = 30;
    protected static final float WIDTH = GameScreen.GAME_WIDTH - 2 * MARGIN;
    protected static final float HEIGHT = GameScreen.GAME_HEIGHT - 2 * MARGIN;
    protected static final float MIN_DIST = 80;

    protected static final Vector2 bottomLeft = new Vector2(MARGIN, MARGIN);
    protected static final Vector2 topRight = new Vector2(MARGIN + WIDTH, MARGIN + HEIGHT);

    protected final Random rand;

    protected SymmetricGenerator(Random rand) {
        this.rand = rand;
    }

    protected void positionPair(Vector2 pos1, Vector2 pos2) {
        while (true) {
            pos1.set(rand.nextFloat() * WIDTH, rand.nextFloat() * HEIGHT);
            pos2.set(topRight).sub(pos1);
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
            if (allOK)
                return;
        }
    }
}
