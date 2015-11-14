package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.NetworkCommunicator.NetworkTokenListener;

public abstract class GameScreenState extends InputAdapter implements NetworkTokenListener {

    static final int RESTART_CODE = 254;

    protected final GameScreen screen;

    protected GameScreenState(GameScreen screen) {
        this.screen = screen;
    }

    /**
     * Called before this state is set as the current one.
     */
    public void onEnter() { }

    /**
     * Update and render the screen.
     * @return What the next State should be
     */
    public abstract GameScreenState render(SpriteBatch batch, float delta);

}
