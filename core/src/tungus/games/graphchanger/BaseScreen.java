package tungus.games.graphchanger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public abstract class BaseScreen implements Screen {

    @SuppressWarnings({"WeakerAccess", "UnusedDeclaration"})
    protected final Game game;

    protected BaseScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public abstract void render(float delta);

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
