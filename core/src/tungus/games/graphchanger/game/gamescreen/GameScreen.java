package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import tungus.games.graphchanger.BaseScreen;
import tungus.games.graphchanger.game.graph.load.GraphLoader;
import tungus.games.graphchanger.game.network.NetworkCommunicator;
import tungus.games.graphchanger.game.players.Player;
import tungus.games.graphchanger.input.BasicTouchWrapper;
import tungus.games.graphchanger.menu.MultiPlayerSetup;
import tungus.games.graphchanger.menu.SinglePlayerSetup;

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

    public GameScreen(Game game, GraphLoader loader) {
        this(game, loader, NetworkCommunicator.dummy(), Player.P1);
    }

    public GameScreen(Game game, GraphLoader loader, NetworkCommunicator comm, Player player) {
        super(game);
        Gdx.app.log("FLOW", "Entered game screen, player " + player.toString());

        this.player = player;

        cam = new OrthographicCamera(GAME_WIDTH, GAME_HEIGHT);
        cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
        cam.update();
        batch = new SpriteBatch(5460);
        batch.setProjectionMatrix(cam.combined);

        this.comm = comm;
        comm.clearListeners();
        userInput = new InputMultiplexer();
        Gdx.input.setInputProcessor(userInput);

        if (comm.isConnected()) {
            // In multiplayer, syncing is needed to start at the same time accurately
            currentState = new SyncState(this, player == Player.P1);
        } else {
            currentState = new CountDownState(this, TimeUtils.millis());
        }

        comm.addListener(currentState);
        userInput.addProcessor(currentState);
        Gdx.app.log("FLOW", "GameScreen comm set up");

        newGame(loader);
        Gdx.app.log("FLOW", "Game loaded");

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
        comm.dispose();
    }

    /**
     * Transitions to the given GameScreenState.
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

    /**
     * Aborts the game, returning to the setup screen (multiplayer or singleplayer logically)
     */
    public void backToSetup() {
        if (comm.isConnected()) {
            game.setScreen(new MultiPlayerSetup(game, comm, player));
        } else {
            game.setScreen(new SinglePlayerSetup(game));
        }
    }
}
