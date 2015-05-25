package tungus.games.graphchanger.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.BasicTouchListener;
import tungus.games.graphchanger.game.editor.GraphEditor;
import tungus.games.graphchanger.game.editor.MoveListenerMultiplexer;
import tungus.games.graphchanger.game.gamestate.GameSimulator;
import tungus.games.graphchanger.game.gamestate.GameState;
import tungus.games.graphchanger.game.network.Connection;
import tungus.games.graphchanger.game.players.Player;

import java.io.InputStream;
import java.io.OutputStream;

class GameController {
    private final GraphEditor editor;
    private final Connection connection;
    private final GameSimulator simulator;

    public GameController(Player player, FileHandle level) {
        this(player, level, null, null);
    }

    public GameController(Player player, FileHandle level, InputStream in, OutputStream out) {
        editor = new GraphEditor(player);
        simulator = new GameSimulator(editor, level);
        if (in != null && out != null) {
            connection = new Connection(in, out);
            editor.setMoveListener(new MoveListenerMultiplexer(simulator, connection));
        } else {
            connection = null;
            editor.setMoveListener(simulator);
        }
        editor.bindGraphInstance(simulator.latestState().graph);
    }

    public void render(float delta, SpriteBatch batch) {
        delta = Math.min(delta, 1/30f);
        if (connection != null) {
            connection.processReceived(simulator);
        }
        if (simulator.update(delta) && connection != null) {
            connection.send();
        }
        GameState current = simulator.latestState();
        editor.bindGraphInstance(current.graph);
        current.render(batch, simulator.timeSinceTick());
    }

    public BasicTouchListener getTouchListener() {
        return editor.input;
    }
}
