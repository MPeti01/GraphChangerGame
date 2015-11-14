package tungus.games.graphchanger.menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.TimeUtils;
import tungus.games.graphchanger.BaseScreen;
import tungus.games.graphchanger.game.gamescreen.GameScreen;
import tungus.games.graphchanger.game.graph.load.EmptyGenerator;
import tungus.games.graphchanger.game.graph.load.FileLoader;
import tungus.games.graphchanger.game.graph.load.GraphLoader.Mode;
import tungus.games.graphchanger.game.graph.load.MixedPositionGenerator;
import tungus.games.graphchanger.game.network.NetworkCommunicator;
import tungus.games.graphchanger.game.network.NetworkCommunicator.NetworkTokenListener;
import tungus.games.graphchanger.game.players.Player;

/**
 * Screen for setting up a multiplayer game, i.e. level/mode selection.
 */
public class MultiPlayerSetup extends BaseScreen {

    /*
      The procedure for choosing the level:

      The client where (player == Player.P1) gets to choose the level.

      When the user input asks for a game, the chooser notifies the other client and they both
      create a GraphLoader accordingly, then set the fields needed for transitioning to GameScreen.

      In the render() function, we are on the main thread (unlike when receiving network input), so
      do the actual transition there.
     */

    private static final long BM_ONE_BYTE = (long) Math.pow(2, 8) - 1;

    private final NetworkCommunicator comm;
    private final Player player;
    private final boolean initiate;

    private volatile Mode mode = null;
    private volatile short seed = 0;

    public MultiPlayerSetup(Game game, NetworkCommunicator comm, Player player) {
        super(game);
        comm.setDetailedLogging(true);
        Gdx.app.log("FLOW", "Entered MP setup screen, player " + player.toString());
        this.comm = comm;
        comm.clearListeners();
        this.player = player;
        initiate = (player == Player.P1);
        if (initiate) {
            Gdx.app.log("FLOW", "Waiting for user input...");
            Gdx.input.setInputProcessor(new InputAdapter() {
                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    if (screenY > Gdx.graphics.getHeight() / 2) {
                        if (screenX > Gdx.graphics.getWidth() / 2) {
                            initGame(Mode.RANDOM_MIXED, (short)TimeUtils.millis());
                        } else {
                            initGame(Mode.RANDOM_EMPTY, (short)TimeUtils.millis());
                        }
                    } else {
                        if (screenX > Gdx.graphics.getWidth() / 2) {
                            initGame(Mode.PERF_TEST, (short)TimeUtils.millis());
                        } else {
                            initGame(Mode.LOAD_FILE, (short)TimeUtils.millis());
                        }
                    }
                    return true;
                }
            });
        } else {
            Gdx.app.log("FLOW", "Waiting for remote input...");
            Gdx.input.setInputProcessor(null);
            comm.addListener(new NetworkTokenListener() {
                @Override
                public boolean receivedMessage(int[] m) {
                    Gdx.app.log("FLOW", "Received remote input, starting game...");
                    // First byte is mode count, then two bytes for the seed.
                    // Reverse of sendStartMessage()
                    mode = Mode.values()[m[0]];
                    seed = (short) (m[2] << 8 | m[1]);
                    Gdx.app.log("FLOW", "Mode " + mode.ordinal() + ", seed " + seed);
                    return true;
                }
            });
        }
    }

    /**
     * Notify the other client and set the data fields (meaning ready to transition to next screen)
     */
    private void initGame(Mode mode, short seed) {
        Gdx.app.log("FLOW", "Received user input, sending message and starting game...");
        Gdx.app.log("FLOW", "Mode " + mode.ordinal() + ", seed " + seed);
        this.mode = mode;
        this.seed = seed;
        sendStartMessage(mode, seed);
    }

    private void sendStartMessage(Mode mode, short seed) {
        // The two bytes must be sent as separate variables, because OutputStream can only send bytes.
        byte lowest = (byte) (seed & BM_ONE_BYTE);
        byte second = (byte) ((seed >> 8) & BM_ONE_BYTE);
        comm.write(mode.ordinal(), lowest, second);
    }

    private void startGame(Mode mode, short seed) {
        switch (mode) {
            case RANDOM_EMPTY:
                game.setScreen(new GameScreen(game, new EmptyGenerator(seed), comm, player));
                break;
            case RANDOM_MIXED:
                game.setScreen(new GameScreen(game, new MixedPositionGenerator(seed), comm, player));
                break;
            case LOAD_FILE:
                game.setScreen(new GameScreen(game, new FileLoader(Gdx.files.internal("levels/random1.lvl")), comm, player));
                break;
            case PERF_TEST:
                game.setScreen(new GameScreen(game, new FileLoader(Gdx.files.internal("levels/perftest.lvl")), comm, player));
                break;
        }
    }

    @Override
    public void render(float delta) {
        if (mode != null) { // Fields are set when ready to go
            startGame(mode, seed);
        }
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
