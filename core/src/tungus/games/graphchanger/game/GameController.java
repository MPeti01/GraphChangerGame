package tungus.games.graphchanger.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.BasicTouchListener;
import tungus.games.graphchanger.game.gamestate.GameSimulator;
import tungus.games.graphchanger.game.gamestate.GameState;
import tungus.games.graphchanger.game.graph.GraphRenderer;
import tungus.games.graphchanger.game.network.Connection;
import tungus.games.graphchanger.game.players.Player;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Coordinates the game's components (network, simulation, rendering, later AI?), dictating when everything should happen.
 */
class GameController {
    private final Connection connection;
    private final GameSimulator simulator;
    private final GraphRenderer graphRenderer = new GraphRenderer();

    /**
     * Constructs a single-player game, currently with a passive other player.
     * @param player Which side the player controls.
     * @param level The file to load the level from
     */
    public GameController(Player player, FileHandle level) {
        this(player, level, null, null);
    }

    public GameController(Player player, FileHandle level, InputStream in, OutputStream out) {
        if (in != null && out != null) {
            connection = new Connection(in, out);
            simulator = new GameSimulator(level, player, connection);
        } else {
            connection = null;
            simulator = new GameSimulator(level, player);
        }
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
        graphRenderer.render(current.graph, current.editor, batch);
        current.renderArmies(batch, simulator.timeSinceTick());
    }

    public BasicTouchListener getTouchListener() {
        return simulator.latestState().editorTouchListener();
    }
}
