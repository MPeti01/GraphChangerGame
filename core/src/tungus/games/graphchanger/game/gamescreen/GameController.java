package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
        delta = Math.min(delta, 1/30f);
        if (connection != null) {
            connection.processReceived(simulator);
        }
        if (simulator.update(delta) && connection != null) {
            connection.send();
        }

        GameState current = simulator.latestState();
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

    public void takeUserInput(boolean inp) {
        gameInput.takeUserInput(inp);
    }
}
