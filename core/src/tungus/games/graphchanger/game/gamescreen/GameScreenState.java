package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.game.network.NetworkCommunicator.NetworkTokenListener;

public interface GameScreenState {

    public void onEnter(GameScreen screen);

    /**
     * Update and render the screen.
     *
     * @return What the next State should be
     */
    public GameScreenState render(GameScreen screen, SpriteBatch batch, float delta);

    public InputProcessor userInputListener();

    public NetworkTokenListener networkInputListener();
}
