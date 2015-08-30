package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import tungus.games.graphchanger.game.graph.load.EmptyGenerator;
import tungus.games.graphchanger.game.graph.load.FileLoader;
import tungus.games.graphchanger.game.graph.load.MixedPositionGenerator;
import tungus.games.graphchanger.game.network.SimpleMessage;

public class StartingState extends GameScreenState {

    private static final long BM_TWO_BYTES = (long) Math.pow(2, 16) - 1;
    private static final long BM_ONE_BYTE = (long) Math.pow(2, 8) - 1;

    private static final long WAIT_TIME_MILLIS = 1500;

    private static final int LOAD_FILE_KEY = Keys.NUM_0;
    private static final int RANDOM_EMPTY_KEY = Keys.NUM_1;
    private static final int RANDOM_MIXED_KEY = Keys.NUM_2;

    private final boolean isInitiator;
    private long startTime;

    public StartingState(GameScreen screen, boolean initor) {
        super(screen);
        isInitiator = initor;
        startTime = Long.MAX_VALUE;
    }

    @Override
    public void onEnter() {
        Gdx.app.log("LIFECYCLE", "Starting state entered, " + (isInitiator ? "waiting for user mode choice" :
                "waiting for remote mode choice"));
    }

    @Override
    public GameScreenState render(SpriteBatch batch, float delta) {
        if (TimeUtils.millis() >= startTime) {
            Gdx.app.log("LIFECYCLE", "Exiting starting state");
            return new PlayingState(screen);
        } else {
            return this;
        }
    }

    @Override
    public boolean receivedMessage(int[] m) {
        if (!isInitiator && startTime == Long.MAX_VALUE) {
            Gdx.app.log("LIFECYCLE", "Received game start message, initiating");
            // Received lower two bits of starting time, combine that with the current
            int timeReceived = m[2] << 8 | m[1];
            startTime = (TimeUtils.millis() & ~BM_TWO_BYTES) | timeReceived;

            // If there happened to be a rollover past the two bytes in these seconds, these bitoperations
            // will have decreased the time. In that case, just add binary 0001_0000_0000.
            if (startTime < TimeUtils.millis())
                startTime += (long) Math.pow(2, 16);

            initGame(m[0]); // The mode code
        }
        return false;
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (isInitiator) {
            switch (keyCode) {
                case RANDOM_MIXED_KEY:
                case RANDOM_EMPTY_KEY:
                    Gdx.app.log("LIFECYCLE", "Received game start key input, initiating");
                    sendStartMessage(keyCode);
                    initGame(keyCode);
                    return true;
                default:
                    return false;
            }
        } else
            return false;
    }

    private void sendStartMessage(int keyCode) {
        startTime = TimeUtils.millis() + WAIT_TIME_MILLIS;
        // Send the most informative, lower 2 bytes. The remote client can figure out the rest.
        // The two bytes must be sent as separate variables, because OutputStream can only send bytes.
        byte lowest = (byte) (startTime & BM_ONE_BYTE);
        byte second = (byte) ((startTime >> 8) & BM_ONE_BYTE);
        screen.comm.write(new SimpleMessage(keyCode, lowest, second));
    }

    private void initGame(int keyCode) {
        Gdx.app.log("LIFECYCLE", "Initiating with start time (seed) " + startTime);
        switch (keyCode) {
            case RANDOM_EMPTY_KEY:
                screen.newGame(new EmptyGenerator((int) startTime));
                break;
            case RANDOM_MIXED_KEY:
                screen.newGame(new MixedPositionGenerator((int) startTime));
                break;
            case LOAD_FILE_KEY:
                screen.newGame(new FileLoader(Gdx.files.internal("levels/random1.lvl")));
                break;
        }
    }

}
