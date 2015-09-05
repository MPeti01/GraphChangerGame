package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.Assets.Tex;
import tungus.games.graphchanger.game.gamestate.GameSimulator;
import tungus.games.graphchanger.game.gamestate.GameState;
import tungus.games.graphchanger.game.graph.GraphRenderer;
import tungus.games.graphchanger.game.graph.editing.GraphEditingUI;
import tungus.games.graphchanger.game.graph.editing.InputInterpreter;
import tungus.games.graphchanger.game.graph.editing.moves.MoveListener;
import tungus.games.graphchanger.game.graph.editing.moves.MoveListenerMultiplexer;
import tungus.games.graphchanger.game.graph.load.GraphLoader;
import tungus.games.graphchanger.game.network.Connection;
import tungus.games.graphchanger.game.network.NetworkCommunicator;
import tungus.games.graphchanger.game.players.Player;
import tungus.games.graphchanger.input.BasicTouchListener;

/**
 * Coordinates the game's components (network, simulation, rendering, later AI?), dictating when everything should happen.
 */
class GameController {

    private final GameSimulator simulator;
    private final Connection connection;
    private final InputInterpreter gameInput;
    private final GraphEditingUI editUI = new GraphEditingUI();
    private final GraphRenderer graphRenderer = new GraphRenderer();

    public GameController(Player player, GraphLoader loader, NetworkCommunicator comm) {
        simulator = new GameSimulator(loader);
        MoveListener moveListener = simulator;
        if (comm != null) {
            connection = new Connection(comm);
            moveListener = new MoveListenerMultiplexer(connection, moveListener);
        } else {
            connection = null;
        }
        gameInput = new InputInterpreter(moveListener, player);
    }

    public void render(float delta, SpriteBatch batch) {
        delta = correctDelta(delta, batch);
        update(delta);
        draw(batch);
    }

    /**
     * Decreases the delta time if needed: if it seems way too large or if the simulator is already
     * lagging behind. The corrected time will be used for updating.
     * Also receives the SpriteBatch to render warning graphics when lagging.
     */
    private float correctDelta(float delta, SpriteBatch batch) {
        delta = Math.min(delta, 0.1f);
        if (simulator.timeSinceTick() > 2 * GameSimulator.TICK_TIME) {
            delta = 0;
            Gdx.app.log("PERF", "Lagging, can't update fast enough! Slowing sim");
            batch.setColor(1, 1, 1, 0.3f);
            batch.draw(Tex.NODE20.t, -1000, -1000, 4000, 4000);
            batch.setColor(1, 1, 1, 1);
        }
        return delta;
    }

    private void update(float delta) {
        simulator.timePassed(delta);
        if (connection != null) {
            connection.processReceived(simulator);
        }
        if (connection == null || !connection.isActive()) {
            simulator.update();
        } else if (connection.shouldSend()) {
            if (simulator.update()) {
                connection.send();
            }
        }
    }

    private void draw(SpriteBatch batch) {
        GameState current = simulator.state();
        gameInput.setGameState(current);
        gameInput.updateUI(editUI);

        graphRenderer.renderEdges(current.graph.edges, current.graph.partialEdges, editUI, batch);
        editUI.renderBehindNodes(batch);
        graphRenderer.renderNodes(current.graph.nodes, editUI, batch);
        editUI.renderOnTop(batch);
        current.renderArmies(batch, simulator.timeSinceTick());
    }

    public BasicTouchListener getTouchListener() {
        return gameInput;
    }

    /**
     * Enables or disables the game accepting user input
     *
     * @param inp true to enable, false to disable
     */
    public void takeUserInput(boolean inp) {
        gameInput.takeUserInput(inp);
    }
}
