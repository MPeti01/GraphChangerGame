package tungus.games.graphchanger.game.gamestate;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.game.graph.EdgePricer;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.graph.editing.moves.Move;
import tungus.games.graphchanger.game.players.Army;

/**
 * Stores and handles all the objects describing a game state: The {@link Graph} and two {@link Army Armies}
 */
public class GameState {
    public final Graph graph;
    public final EdgePricer edgePricer;
    private final Army p1;
    private final Army p2;

    public GameState(Graph graph, EdgePricer pricer, Army p1, Army p2) {
        this.graph = graph;
        this.edgePricer = pricer;
        this.p1 = p1;
        this.p2 = p2;
    }

    public void applyMove(Move m) {
        m.applyTo(graph);
    }

    public void update(float delta) {
        graph.updateNodes(delta, p1, p2);
        p1.updateUnits(delta);
        p2.updateUnits(delta);
    }

    public void renderArmies(SpriteBatch batch, float sinceTick) {
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        p1.renderUnits(batch, sinceTick);
        p2.renderUnits(batch, sinceTick);
    }

    public void set(GameState other) {
        this.graph.set(other.graph);
        this.edgePricer.set(other.edgePricer);
        this.p1.set(other.p1, this.graph);
        this.p2.set(other.p2, this.graph);
    }
}
