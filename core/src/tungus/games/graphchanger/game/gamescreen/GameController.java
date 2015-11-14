package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import tungus.games.graphchanger.Assets.Tex;
import tungus.games.graphchanger.game.gamestate.GameSimulator;
import tungus.games.graphchanger.game.gamestate.GameState;
import tungus.games.graphchanger.game.graph.GraphRenderer;
import tungus.games.graphchanger.game.graph.editing.GraphEditingUI;
import tungus.games.graphchanger.game.graph.editing.InputInterpreter;
import tungus.games.graphchanger.game.graph.editing.moves.MoveListener;
import tungus.games.graphchanger.game.graph.editing.moves.MoveListenerMultiplexer;
import tungus.games.graphchanger.game.graph.load.GraphLoader;
import tungus.games.graphchanger.game.opponent.OpponentConnection;
import tungus.games.graphchanger.game.players.Player;
import tungus.games.graphchanger.input.BasicTouchListener;

/**
 * Coordinates the game's components (network, simulation, rendering, later AI?), dictating when everything should happen.
 */
class GameController {

    private final GameSimulator simulator;
    private final OpponentConnection opponent;
    private final InputInterpreter gameInput;
    private final GraphEditingUI editUI = new GraphEditingUI();
    private final GraphRenderer graphRenderer = new GraphRenderer();

    public GameController(Player player, GraphLoader loader, OpponentConnection opponent) {
        this.opponent = opponent;
        simulator = new GameSimulator(loader);

        MoveListener moveListener = new MoveListenerMultiplexer(opponent, simulator);
        gameInput = new InputInterpreter(moveListener, player);
    }

    /**
     * Decreases the delta time if needed: if it seems way too large or if the simulator is already
     * lagging behind. The corrected time will be used for updating.
     * Also receives the SpriteBatch to render warning graphics when lagging.
     */
    private float correctDelta(float delta, SpriteBatch batch) {
        delta = MathUtils.clamp(delta, 0.001f, 0.1f);
        if (simulator.timeSinceTick() > 2 * GameSimulator.TICK_TIME) {
            delta = 0;
            Gdx.app.log("PERF", "Lagging, can't update fast enough! Slowing sim");
            batch.setColor(1, 0, 0, 0.3f);
            batch.draw(Tex.LINE.t, -1000, -1000, 4000, 4000);
            batch.setColor(1, 1, 1, 1);
        }
        return delta;
    }

    public void update(SpriteBatch batch, float delta) {
        delta = correctDelta(delta, batch);
        simulator.timePassed(delta);
        if (!opponent.isActive()) {
            simulator.update();
        } else {
            opponent.processReceived(simulator);
            if (opponent.shouldSend()) {
                if (simulator.update()) {
                    opponent.send();
                }
            }
        }
    }

    public void render(SpriteBatch batch, float delta) {
        GameState current = simulator.state();
        gameInput.setGameState(current);
        gameInput.updateUI(editUI);

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        graphRenderer.renderEdges(current.graph.edges, current.graph.partialEdges, editUI, batch, delta);
        editUI.renderBehindNodes(batch);
        graphRenderer.renderNodes(current.graph.nodes, editUI, batch, delta);
        editUI.renderOnTop(batch);
        current.renderArmies(batch, simulator.timeSinceTick());
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public BasicTouchListener getTouchListener() {
        return gameInput;
    }

    /**
     * Enables or disables the game accepting user input
     * @param inp true to enable, false to disable
     */
    public void takeUserInput(boolean inp) {
        gameInput.takeUserInput(inp);
    }
}
