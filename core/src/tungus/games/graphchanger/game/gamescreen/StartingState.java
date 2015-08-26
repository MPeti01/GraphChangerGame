package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.game.network.NetworkCommunicator.NetworkTokenListener;

public class StartingState implements GameScreenState {

    public StartingState(boolean seed) {

    }

    @Override
    public void onEnter(GameScreen screen) {
        screen.newGame();
    }

    @Override
    public GameScreenState render(GameScreen screen, SpriteBatch batch, float delta) {
        return new PlayingState();
    }

    private InputProcessor userInput = new InputAdapter();
    private NetworkTokenListener remoteInput = new NetworkTokenListener() {
        @Override
        public boolean receivedMessage(int[] m) {
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
