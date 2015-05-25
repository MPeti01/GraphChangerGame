package tungus.games.graphchanger.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import tungus.games.graphchanger.BaseScreen;
import tungus.games.graphchanger.game.players.Player;

import java.io.InputStream;
import java.io.OutputStream;

public class GameScreen extends BaseScreen {

    private final SpriteBatch batch;
    private final Camera cam;
    private final GameController gameController;

    @SuppressWarnings("FieldCanBeLocal")
    private final InputProcessor input = new InputAdapter() {
        private final Vector3 touch3 = new Vector3();
        private final Vector2 touch2 = new Vector2();
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            gameController.getTouchListener().onDown(unproject(screenX, screenY));
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            gameController.getTouchListener().onUp(unproject(screenX, screenY));
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            gameController.getTouchListener().onDrag(unproject(screenX, screenY));
            return false;
        }

        private Vector2 unproject(int x, int y) {
            touch3.set(x, y, 0);
            cam.unproject(touch3);
            touch2.set(touch3.x, touch3.y);
            return touch2;
        }
    };

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
        Gdx.input.setInputProcessor(input);
        gameController = new GameController(player, Gdx.files.internal("levels/random1.lvl"), in, out);
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        gameController.render(delta, batch);
        batch.end();
    }
}
