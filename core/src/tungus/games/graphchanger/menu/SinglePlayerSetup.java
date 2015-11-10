package tungus.games.graphchanger.menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.TimeUtils;
import tungus.games.graphchanger.BaseScreen;
import tungus.games.graphchanger.game.gamescreen.GameScreen;
import tungus.games.graphchanger.game.graph.load.EmptyGenerator;
import tungus.games.graphchanger.game.graph.load.FileLoader;
import tungus.games.graphchanger.game.graph.load.GraphLoader.Mode;
import tungus.games.graphchanger.game.graph.load.MixedPositionGenerator;

/**
 * Screen for single player game setup: Level selection, maybe AI difficulty etc.
 */
public class SinglePlayerSetup extends BaseScreen {
    public SinglePlayerSetup(Game game) {
        super(game);
        Gdx.app.log("FLOW", "Entered SP setup screen");
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (screenY > Gdx.graphics.getHeight() / 2) {
                    if (screenX > Gdx.graphics.getWidth() / 2) {
                        startGame(Mode.RANDOM_MIXED);
                    } else {
                        startGame(Mode.RANDOM_EMPTY);
                    }
                } else {
                    if (screenX > Gdx.graphics.getWidth() / 2) {
                        startGame(Mode.PERF_TEST);
                    } else {
                        startGame(Mode.LOAD_FILE);
                    }
                }
                return true;
            }
        });
    }

    private void startGame(Mode mode) {
        Gdx.app.log("FLOW", "Starting SP game...");
        short seed = (short) TimeUtils.millis();
        switch (mode) {
            case RANDOM_EMPTY:
                game.setScreen(new GameScreen(game, new EmptyGenerator(seed)));
                break;
            case RANDOM_MIXED:
                game.setScreen(new GameScreen(game, new MixedPositionGenerator(seed)));
                break;
            case LOAD_FILE:
                game.setScreen(new GameScreen(game, new FileLoader(Gdx.files.internal("levels/random1.lvl"))));
                break;
            case PERF_TEST:
                game.setScreen(new GameScreen(game, new FileLoader(Gdx.files.internal("levels/perftest.lvl"))));
                break;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
