package tungus.games.graphchanger.game.gamestate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.BasicTouchListener;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.graph.editor.GraphEditingUI;
import tungus.games.graphchanger.game.graph.editor.Move;
import tungus.games.graphchanger.game.players.Army;
import tungus.games.graphchanger.game.players.UnitCollisionChecker;

/**
 * Stores and handles all the objects describing a game state: The {@link Graph} and two {@link Army Armies}
 */
public class GameState {
    public final Graph graph;
    public final GraphEditingUI editor;
    public final Army p1;
    @SuppressWarnings("WeakerAccess")
    public final Army p2;

    public GameState(Graph graph, GraphEditingUI editor, Army p1, Army p2) {
        this.graph = graph;
        this.editor = editor;
        this.p1 = p1;
        this.p2 = p2;
    }

    public void set(GameState other) {
        graph.set(other.graph);
        editor.set(other.editor);
        p1.set(other.p1, graph.nodes);
        p2.set(other.p2, graph.nodes);
    }

    public void set(Graph graph, GraphEditingUI editor, Army p1, Army p2) {
        this.graph.set(graph);
        this.editor.set(editor);
        this.p1.set(p1, this.graph.nodes);
        this.p2.set(p2, this.graph.nodes);
    }

    public void applyMove(Move m) {
        m.applyTo(graph);
    }

    public void update(float delta, UnitCollisionChecker unitCollider) {
        graph.updateNodes(delta, p1, p2);
        editor.update(delta);
        unitCollider.removeColliders(p1, p2);
        p1.updateUnits(delta);
        p2.updateUnits(delta);
    }

    public void renderArmies(SpriteBatch batch, float sinceTick) {
        p1.renderUnits(batch, sinceTick);
        p2.renderUnits(batch, sinceTick);
    }

    public BasicTouchListener editorTouchListener() {
        return editor.input;
    }
}
