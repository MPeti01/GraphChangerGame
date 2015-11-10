package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import tungus.games.graphchanger.Assets.Tex;

/**
 * The GameState just before the game starts: Fade in, countdown (TODO actual countdown)
 */
public class CountDownState extends GameScreenState {

    private static final long COUNTDOWN_TIME = 4000;
    private final long gameStartTime;

    public CountDownState(GameScreen screen, long countStartTime) {
        super(screen);
        Gdx.app.log("FLOW", "Entered countdown state");
        this.gameStartTime = countStartTime + COUNTDOWN_TIME;
    }

    @Override
    public GameScreenState render(SpriteBatch batch, float delta) {
        batch.begin();
        screen.gameController.render(batch, delta);
        float p = MathUtils.clamp((gameStartTime - TimeUtils.millis())/(float)COUNTDOWN_TIME, 0, 1);
        batch.setColor(0, 0, 0, p);
        batch.draw(Tex.LINE.t, -100, -100, 1000, 1000);
        batch.setColor(1, 1, 1, 1);
        batch.end();

        if (TimeUtils.millis() > gameStartTime) {
            Gdx.app.log("FLOW", "Starting game!");
            return new PlayingState(screen);
        } else {
            return this;
        }
    }

    @Override
    public boolean receivedMessage(int[] m) {
        return false;
    }
}
