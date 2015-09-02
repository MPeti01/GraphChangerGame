package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import tungus.games.graphchanger.game.graph.load.EmptyGenerator;
import tungus.games.graphchanger.game.graph.load.FileLoader;
import tungus.games.graphchanger.game.graph.load.MixedPositionGenerator;
import tungus.games.graphchanger.game.network.SimpleMessage;

/**
 * Active state before starting the game. Responsible for choosing the game parameters, communicating these
 * and starting the game at the same time on both clients.
 */
public class StartingState extends GameScreenState {

    /*
       The method for starting the game is as follows:
       The initiating client (where isInitiator == true) takes user input. It detects when the user chose
       to start, and sets the seed for generators to the last two bytes of the current time (millis) and
       the mode number to whatever the user has chosen.

       It sends this information to the non-initiating client (where isInitiator == false). The non-initiating
       client sets up the game by these parameters, sets the starting time to WAIT_TIME_MILLIS ms from this
       moment, then sends a confirmation message to the initiating client.

       The initiating client assumes that the other client received the start information halfway between
       this moment and when it was originally sent (i.e. the two messages took equal time to deliver).
       From this, it calculates when the other client is going to start the game and sets its own start
       time to the same.
     */

    private static final long BM_ONE_BYTE = (long) Math.pow(2, 8) - 1;

    private static final long WAIT_TIME_MILLIS = 1500;

    /**
     * Whether this client gets to choose the game setup options. Should always be true for exactly
     * one of two connected clients.
     */
    private final boolean isInitiator;
    private long gameStartTime; // When the game should be started
    private long startInputTime;// When the user initiated the game start. Only used if (isInitiator == true)
    /**
     * Identifier determining how the map should be created.
     */
    private int modeNumber;

    private static final int MODE_LOAD_FILE = Keys.NUM_0;
    private static final int MODE_RANDOM_EMPTY = Keys.NUM_1;
    private static final int MODE_RANDOM_MIXED = Keys.NUM_2;

    /**
     * The seed used for map generation if randomness is needed.
     */
    private short seed;

    public StartingState(GameScreen screen, boolean initiator) {
        super(screen);
        isInitiator = initiator;
        gameStartTime = startInputTime = Long.MAX_VALUE;
    }

    @Override
    public void onEnter() {
        Gdx.app.log("LIFECYCLE", "Starting state entered, " + (isInitiator ? "waiting for user mode choice" :
                "waiting for remote mode choice"));
    }

    @Override
    public GameScreenState render(SpriteBatch batch, float delta) {
        if (TimeUtils.millis() >= gameStartTime) {
            Gdx.app.log("LIFECYCLE", "Exiting starting state");
            return new PlayingState(screen);
        } else {
            return this;
        }
    }

    @Override
    public boolean receivedMessage(int[] m) {
        if (!isInitiator && gameStartTime == Long.MAX_VALUE) {
            // The initiating client wants to start and sent us the game parameters
            Gdx.app.log("LIFECYCLE", "Received game start message, initiating");
            modeNumber = m[0];
            seed = (short) (m[2] << 8 | m[1]);
            initGame();

            gameStartTime = TimeUtils.millis() + WAIT_TIME_MILLIS;
            Gdx.app.log("LIFECYCLE", "Starting at " + gameStartTime + " (currently " + TimeUtils.millis() + ")");

            // Send confirmation message
            screen.comm.write(new SimpleMessage(0, 0, 0));
            return true;
        } else if (isInitiator && startInputTime != Long.MAX_VALUE) {
            // Confirmation message received from non-initiating client, calculate start time
            gameStartTime = (TimeUtils.millis() + startInputTime) / 2 + WAIT_TIME_MILLIS;
            Gdx.app.log("LIFECYCLE", "Received game start confirmation, starting at " + gameStartTime
                    + " (currently " + TimeUtils.millis() + ")");
            return true;
        } else
            return false;
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (isInitiator) {
            switch (keyCode) {
                case MODE_RANDOM_MIXED:
                case MODE_RANDOM_EMPTY:
                    Gdx.app.log("LIFECYCLE", "Received game start key input, notifying remote and initiating");
                    startInputTime = TimeUtils.millis();
                    modeNumber = keyCode;
                    seed = (short) TimeUtils.millis();
                    sendStartMessage();
                    initGame();
                    return true;
                default:
                    return false;
            }
        } else
            return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (screenY > Gdx.graphics.getHeight() / 2) {
            if (screenX > Gdx.graphics.getWidth() / 2) {
                keyDown(MODE_RANDOM_MIXED);
            } else {
                keyDown(MODE_RANDOM_EMPTY);
            }
            return true;
        } else {
            return false;
        }
    }

    private void sendStartMessage() {
        // The two bytes must be sent as separate variables, because OutputStream can only send bytes.
        byte lowest = (byte) (seed & BM_ONE_BYTE);
        byte second = (byte) ((seed >> 8) & BM_ONE_BYTE);
        screen.comm.write(new SimpleMessage(modeNumber, lowest, second));
    }

    private void initGame() {
        Gdx.app.log("LIFECYCLE", "Setting up game with seed " + seed);
        switch (modeNumber) {
            case MODE_RANDOM_EMPTY:
                screen.newGame(new EmptyGenerator(seed));
                break;
            case MODE_RANDOM_MIXED:
                screen.newGame(new MixedPositionGenerator(seed));
                break;
            case MODE_LOAD_FILE:
                screen.newGame(new FileLoader(Gdx.files.internal("levels/random1.lvl")));
                break;
            default:
                throw new IllegalArgumentException("Unknown mode number " + modeNumber);
        }
    }

}
