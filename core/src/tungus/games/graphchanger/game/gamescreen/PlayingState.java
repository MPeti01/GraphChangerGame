package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import tungus.games.graphchanger.game.network.SimpleMessage;

/**
 * A State used when the game is unpaused, in progress.
 */
public class PlayingState extends GameScreenState {

    /**
     * The next state we are transistioning to. Kept as null until transistion begins.
     */
    private GameScreenState next = null;
    /**
     * Whether the next state can be entered right now or we are waiting for the remote client
     * (when next != null)
     */
    private boolean goNext = false;

    public PlayingState(GameScreen screen) {
        super(screen);

    }

    @Override
    public void onEnter() {
        Gdx.app.log("LIFECYCLE", "Playing state entered");
        screen.gameController.takeUserInput(true);
    }

    @Override
    public GameScreenState render(SpriteBatch batch, float delta) {
        if (next == null) {
            // Not in the progress of transistioning to a new State.
            batch.begin();
            screen.gameController.render(delta, batch);
            batch.end();
        }
        return goNext ? next : this;
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (keyCode == Keys.R) {
            // Restart
            if (next == null) {
                // Restarting didn't begin yet -> initiate.
                initiateRestart();
            }
            return true;
        } else return false;
    }

    @Override
    public boolean receivedMessage(int[] m) {
        if (m[0] == RESTART_CODE) {
            if (next == null) {
                // Restarting didn't begin yet, so the remote client initiated it.
                confirmRestart();
            }
            Gdx.app.log("LIFECYCLE", "Restarting");
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
                    && next == null) {
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
        next = new StartingState(screen, true);
        screen.comm.write(new SimpleMessage(RESTART_CODE));
        screen.gameController.takeUserInput(false);
        Gdx.app.log("LIFECYCLE", "Restart requested");
    }

    /**
     * To be called when the remote client requests a restart. Creates the next state and
     * confirms the restart action to the remote client.
     */
    private void confirmRestart() {
        next = new StartingState(screen, false);
        screen.comm.write(new SimpleMessage(RESTART_CODE));
        screen.gameController.takeUserInput(false);
    }

}
