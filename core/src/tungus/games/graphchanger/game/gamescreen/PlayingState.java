package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.game.network.NetworkCommunicator.NetworkTokenListener;
import tungus.games.graphchanger.game.network.SimpleMessage;

/**
 * A State used when the game is unpaused, in progress.
 */
public class PlayingState implements GameScreenState {

    private static final int RESTART_CODE = 254;

    private GameScreen screen = null;
    private GameScreenState next = null;
    private boolean goNext = false;

    @Override
    public void onEnter(GameScreen screen) {
        this.screen = screen;
        screen.gameController.takeUserInput(true);
    }

    @Override
    public GameScreenState render(GameScreen screen, SpriteBatch batch, float delta) {
        batch.begin();
        screen.gameController.render(delta, batch);
        batch.end();
        return goNext ? next : this;
    }

    private final InputProcessor userInput = new InputAdapter() {
        @Override
        public boolean keyDown(int keyCode) {
            if (keyCode == Keys.R) {
                if (next == null) {
                    next = new StartingState(true);
                    screen.comm.write(new SimpleMessage(RESTART_CODE));
                    screen.gameController.takeUserInput(false);
                }
                return true;
            } else return false;
        }
    };

    private final NetworkTokenListener remoteInput = new NetworkTokenListener() {
        @Override
        public boolean receivedMessage(int[] m) {
            if (m[0] == RESTART_CODE) {
                if (next == null) {
                    next = new StartingState(false);
                    screen.comm.write(new SimpleMessage(RESTART_CODE));
                    screen.gameController.takeUserInput(false);
                }
                goNext = true;
                return true;
            }
            return false;
        }
    };

    @Override
    public InputProcessor userInputListener() {
        return userInput;
    }

    @Override
    public NetworkTokenListener networkInputListener() {
        return remoteInput;
    }
}
