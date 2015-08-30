package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.BaseScreen;
import tungus.games.graphchanger.GraphChanger;
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
    private final InputMultiplexer userInput;

    private final Player player;

    final NetworkCommunicator comm;

    GameController gameController;
    private GameScreenState currentState;

    public GameScreen(Game game) {
        this(game, Player.P1, null, null);
    }

    public GameScreen(Game game, Player player, InputStream in, OutputStream out) {
        super(game);
        cam = new OrthographicCamera(GAME_WIDTH, GAME_HEIGHT);
        cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
        cam.update();
        batch = new SpriteBatch();
        batch.setProjectionMatrix(cam.combined);

        this.player = player;
        mp = (in != null);

        comm = new NetworkCommunicator(in, out);
        userInput = new InputMultiplexer();
        Gdx.input.setInputProcessor(userInput);

        currentState = new StartingState(this, player == Player.P1);
        comm.addListener(currentState);
        userInput.addProcessor(currentState);

        currentState.onEnter();
    }
    private final boolean mp;
    private float time = 0;

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        time += delta;
        // The reason for this ugliness is that the BT multiplayer screen cannot be the first.
        // Should be removed once there's a main menu.
        if (Gdx.app.getType() == Application.ApplicationType.Android && !mp) {
            if (time > 1) {
                try {
                    game.setScreen((Screen) (GraphChanger.mpScreen.getConstructors()[0].newInstance(game)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            GameScreenState next = currentState.render(batch, delta);
            if (next != currentState) {
                setState(next);
            }
        }
    }

    @Override
    public void dispose() {
        comm.dispose();
    }

    public void setState(GameScreenState next) {
        comm.removeListener(currentState);
        userInput.removeProcessor(currentState);

        comm.addListener(0, next); // To the front!
        userInput.addProcessor(0, next);

        next.onEnter();
        currentState = next;
    }

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
