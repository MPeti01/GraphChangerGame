package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.game.network.SimpleMessage;

/**
 * A State used when the game is unpaused, in progress.
 */
public class PlayingState extends GameScreenState {

    private GameScreenState next = null;
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
            batch.begin();
            screen.gameController.render(delta, batch);
            batch.end();
        }
        return goNext ? next : this;
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (keyCode == Keys.R) {
            if (next == null) {
                next = new StartingState(screen, true);
                screen.comm.write(new SimpleMessage(RESTART_CODE));
                screen.gameController.takeUserInput(false);
                Gdx.app.log("LIFECYCLE", "Restart requested");
            }
            return true;
        } else return false;
    }

    @Override
    public boolean receivedMessage(int[] m) {
        if (m[0] == RESTART_CODE) {
            if (next == null) {
                next = new StartingState(screen, false);
                screen.comm.write(new SimpleMessage(RESTART_CODE));
                screen.gameController.takeUserInput(false);
            }
            Gdx.app.log("LIFECYCLE", "Restarting");
            goNext = true;
            return true;
        }
        return false;
    }

}
