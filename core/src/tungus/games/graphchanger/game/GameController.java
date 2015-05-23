package tungus.games.graphchanger.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.BasicTouchListener;
import tungus.games.graphchanger.game.editor.GraphEditor;
import tungus.games.graphchanger.game.gamestate.GameSimulator;
import tungus.games.graphchanger.game.gamestate.GameState;
import tungus.games.graphchanger.game.players.Player;

class GameController {
    private final GraphEditor editor;
    private final GameSimulator simulator;

    public GameController(Player player) {
        editor = new GraphEditor(player);
        simulator = new GameSimulator(editor, "levels/whatever..");
        editor.setMoveListener(simulator);
        editor.bindGraphInstance(simulator.latestState().graph);
    }
    public void render(float delta, SpriteBatch batch) {
        simulator.update(delta);
        GameState current = simulator.latestState();
        editor.bindGraphInstance(current.graph);
        current.render(batch, simulator.timeSinceTick());
    }

    public BasicTouchListener getTouchListener() {
        return editor.input;
    }
}
