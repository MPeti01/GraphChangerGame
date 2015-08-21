package tungus.games.graphchanger.game.gamestate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.game.graph.EdgePricer;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.graph.editing.moves.Move;
import tungus.games.graphchanger.game.players.Army;
import tungus.games.graphchanger.game.players.UnitCollisionChecker;

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

    public void set(GameState other) {
        set(other.graph, other.edgePricer, other.p1, other.p2);
    }

    public void set(Graph graph, EdgePricer pricer, Army p1, Army p2) {
        this.graph.set(graph);
        this.edgePricer.set(pricer);
        this.p1.set(p1, this.graph.nodes);
        this.p2.set(p2, this.graph.nodes);
    }

    public void applyMove(Move m) {
        m.applyTo(graph);
    }

    public void update(float delta, UnitCollisionChecker unitCollider) {
        graph.updateNodes(delta, p1, p2);
        unitCollider.removeColliders(p1, p2);
        p1.updateUnits(delta);
        p2.updateUnits(delta);
    }

    public void renderArmies(SpriteBatch batch, float sinceTick) {
        p1.renderUnits(batch, sinceTick);
        p2.renderUnits(batch, sinceTick);
    }
}
