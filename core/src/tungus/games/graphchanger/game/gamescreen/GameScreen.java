package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.BaseScreen;
import tungus.games.graphchanger.game.graph.load.GraphLoader;
import tungus.games.graphchanger.game.network.NetworkCommunicator;
import tungus.games.graphchanger.game.players.Player;
import tungus.games.graphchanger.input.BasicTouchWrapper;

import java.io.InputStream;
import java.io.OutputStream;

public class GameScreen extends BaseScreen {

    public static final float GAME_WIDTH = 480;
    public static final float GAME_HEIGHT = 800;

    private final SpriteBatch batch;
    private final Camera cam;

    /**
     * The main InputProcessor for this Screen. Set up to delegate
     * firstly to the current GameScreenState (for UI and game flow control),
     * secondly to the active GameController (for game input).
     */
    private final InputMultiplexer userInput;

    /**
     * Which player this client plays as
     */
    private final Player player;

    /**
     * Used for communicating with the remote client if there is one.
     * Set up to offer incoming messages
     * firstly to the current GameScreenState (for UI and game flow control),
     * secondly to the active GameController (for game input).
     */
    final NetworkCommunicator comm;

    GameController gameController;
    private GameScreenState currentState;

    // TODO Handle this (SP) properly
    public GameScreen(Game game) {
        this(game, Player.P1, null, null);
    }

    public GameScreen(Game game, Player player, InputStream in, OutputStream out) {
        super(game);
        cam = new OrthographicCamera(GAME_WIDTH, GAME_HEIGHT);
        cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
        cam.update();
        batch = new SpriteBatch(5460);
        batch.setProjectionMatrix(cam.combined);

        this.player = player;
        comm = new NetworkCommunicator(in, out);
        userInput = new InputMultiplexer();
        Gdx.input.setInputProcessor(userInput);

        currentState = new StartingState(this, player == Player.P1);
        comm.addListener(currentState);
        userInput.addProcessor(currentState);

        currentState.onEnter();
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        GameScreenState next = currentState.render(batch, delta);
        if (next != currentState) {
            setState(next);
        }
    }

    @Override
    public void dispose() {
        Gdx.app.log("DEBUG", "dispose()");
        comm.dispose();
    }

    /**
     * Transistions to the given GameScreenState.
     * Binds the user and network inputs to this state from the previous one.
     */
    private void setState(GameScreenState next) {
        comm.removeListener(currentState);
        userInput.removeProcessor(currentState);

        comm.addListener(0, next); // To the front!
        userInput.addProcessor(0, next);

        next.onEnter();
        currentState = next;
    }

    /**
     * Creates a new GameController that uses the given GraphLoader for its initial setup.
     * Binds the user and network inputs to this instance from the previous one.
     */
    public void newGame(GraphLoader loader) {
        if (gameController != null) {
            comm.removeListener(1);
            userInput.removeProcessor(1);
        }

        gameController = new GameController(player, loader, comm);

        BasicTouchWrapper inputToGame = new BasicTouchWrapper(gameController.getTouchListener());
        inputToGame.setCamera(cam);
        userInput.addProcessor(inputToGame);
    }
}
