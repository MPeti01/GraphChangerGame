package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.game.graph.load.FileLoader;

public class StartingState extends GameScreenState {

    public StartingState(GameScreen screen, boolean seed) {
        super(screen);
    }

    @Override
    public void onEnter() {
        Gdx.app.log("LIFECYCLE", "Starting state entered");
        screen.newGame(new FileLoader(Gdx.files.internal("levels/random1.lvl")));
    }

    @Override
    public GameScreenState render(SpriteBatch batch, float delta) {
        return new PlayingState(screen);
    }

    @Override
    public boolean receivedMessage(int[] m) {
        return false;
    }

}
