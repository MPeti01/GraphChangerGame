package tungus.games.graphchanger.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.BaseScreen;
import tungus.games.graphchanger.GraphChanger;
import tungus.games.graphchanger.game.players.Player;
import tungus.games.graphchanger.input.BasicTouchWrapper;

import java.io.InputStream;
import java.io.OutputStream;

public class GameScreen extends BaseScreen {

    private final SpriteBatch batch;
    private final Camera cam;
    private final GameController gameController;

    public GameScreen(Game game) {
        this(game, Player.P1, null, null);
    }

    public GameScreen(Game game, Player player, InputStream in, OutputStream out) {
        super(game);
        cam = new OrthographicCamera(480, 800);
        cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
        cam.update();
        batch = new SpriteBatch();
        batch.setProjectionMatrix(cam.combined);

        mp = (in != null);
        gameController = new GameController(player, Gdx.files.internal("levels/random1.lvl"), in, out);

        BasicTouchWrapper input = new BasicTouchWrapper(gameController.getTouchListener());
        input.setCamera(cam);
        Gdx.input.setInputProcessor(input);
    }
    private final boolean mp;
    private float time = 0;
    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        time += delta;
        if (Gdx.app.getType() == Application.ApplicationType.Android && !mp)
        {
            if (time > 1) {
                try {
                    game.setScreen((Screen)(GraphChanger.mpScreen.getConstructors()[0].newInstance(game)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            batch.begin();
            gameController.render(delta, batch);
            batch.end();
        }
    }

    @Override
    public void dispose() {
        gameController.dispose();
    }
}
