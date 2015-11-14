package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;

/**
 * A State used when the game is unpaused, in progress.
 */
public class PlayingState extends GameScreenState {

    /**
     * true when a restart was initiated
     */
    private volatile boolean restarting = false;
    /**
     * Whether the restart can be done right now or we are waiting for the remote client
     */
    private boolean goNext = false;

    public PlayingState(GameScreen screen) {
        super(screen);
    }

    @Override
    public void onEnter() {
        Gdx.app.log("LIFECYCLE", "Playing state entered");
        screen.comm.setDetailedLogging(false);
        screen.gameController.takeUserInput(true);
    }

    @Override
    public GameScreenState render(SpriteBatch batch, float delta) {
        if (!restarting) {
            // Not in the progress of transitioning to a new State.
            batch.begin();
            screen.gameController.update(batch, delta);
            screen.gameController.render(batch, delta);
            batch.end();
        }
        if (goNext) {
            screen.backToSetup();
        }
        return this;
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (keyCode == Keys.R) {
            // Restart
            if (!restarting) {
                // Restarting didn't begin yet -> initiate.
                initiateRestart();
            }
            return true;
        } else return false;
    }

    @Override
    public boolean receivedMessage(int[] m) {
        if (m[0] == RESTART_CODE) {
            if (!restarting) {
                // Restarting didn't begin yet, so the remote client initiated it.
                confirmRestart();
            }
            Gdx.app.log("FLOW", "Restarting the game, transitioning to setup screen");
            // Either way, the remote client is ready to go as well, so enter the new state.
            goNext = true;
            return true;
        }
        return false;
    }

    private final GestureDetector gestureDetector = new GestureDetector(new GestureAdapter() {
        @Override
        public boolean tap(float x, float y, int count, int button) {
            if (count >= 3 && x < Gdx.graphics.getWidth() / 10 && y < Gdx.graphics.getHeight() / 10
                    && !restarting) {
                initiateRestart();
                return true;
            } else
                return false;
        }
    });

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return gestureDetector.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return gestureDetector.touchUp(screenX, screenY, pointer, button);
    }

    /**
     * To be called when the user input asks for a restart. Sets up the next state and notifies the remote client.
     * Does not actually restart before receiving confirmation from the remote.
     */
    private void initiateRestart() {
        restarting = true;
        if (screen.comm.isConnected()) {
            screen.comm.write(RESTART_CODE);
        } else {
            goNext = true;
        }

        screen.gameController.takeUserInput(false);
        Gdx.app.log("FLOW", "Restart requested");
    }

    /**
     * To be called when the remote client requests a restart. Creates the next state and
     * confirms the restart action to the remote client.
     */
    private void confirmRestart() {
        restarting = true;
        screen.comm.write(RESTART_CODE);
        screen.gameController.takeUserInput(false);
    }

}
