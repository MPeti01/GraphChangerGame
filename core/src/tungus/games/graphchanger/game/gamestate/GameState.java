package tungus.games.graphchanger.game.gamestate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.game.editor.Move;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.players.Army;

/**
 * Stores and handles all the objects describing a game state: The {@link Graph} and two {@link Army Armies}.
 */
public class GameState {
    public final Graph graph;
    @SuppressWarnings("WeakerAccess")
    public final Army p1;
    @SuppressWarnings("WeakerAccess")
    public final Army p2;

    public GameState(Graph graph, Army p1, Army p2) {
        this.graph = graph;
        this.p1 = p1;
        this.p2 = p2;
    }

    public void set(GameState other) {
        graph.set(other.graph, p1, p2);
        p1.set(other.p1, graph);
        p2.set(other.p2, graph);
    }

    public void set(Graph graph, Army p1, Army p2) {
        this.graph.set(graph, this.p1, this.p2);
        this.p1.set(p1, graph);
        this.p2.set(p2, graph);
    }

    public void applyMove(Move m) {
        graph.applyMove(m);
    }

    public void update(float delta) {
        graph.updateNodes(delta);
        p1.updateUnits(delta);
        p2.updateUnits(delta);
    }

    public void render(SpriteBatch batch, float sinceTick) {
        //TODO Take interpolation into account!
        graph.render(batch, sinceTick);
        p1.renderUnits(batch, sinceTick);
        p2.renderUnits(batch, sinceTick);
    }
}
