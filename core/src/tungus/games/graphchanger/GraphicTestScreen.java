package tungus.games.graphchanger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.Assets.Tex;
import tungus.games.graphchanger.drawutils.DistanceParticleEmitter;

import java.io.IOException;

/**
 * Screen for testing graphics developments
 */
public class GraphicTestScreen extends BaseScreen {

    private final ParticleEffect edge;
    private final ParticleEffect node;
    private final ParticleEffect node2;
    private final SpriteBatch batch;

    public GraphicTestScreen(Game g) {
        super(g);
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Keys.R) {
                    game.setScreen(new GraphicTestScreen(game));
                    return true;
                } else {
                    return false;
                }
            }
        });
        ParticleEmitter emitter;
        try {
            emitter = new DistanceParticleEmitter(Gdx.files.internal("particles/edge"), 300);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GdxRuntimeException("Couldn't find particle effect!");
        }
        edge = new ParticleEffect();
        edge.getEmitters().add(emitter);
        Camera cam = new OrthographicCamera(480, 800);
        cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
        cam.update();
        batch = new SpriteBatch();
        batch.setProjectionMatrix(cam.combined);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        edge.setPosition(240, 100);
        edge.loadEmitterImages(Assets.atlas);
        edge.setEmittersCleanUpBlendFunction(false);

        node = new ParticleEffect();
        node.load(Gdx.files.internal("particles/node"), Assets.atlas);
        node.setEmittersCleanUpBlendFunction(false);

        node2 = new ParticleEffect(node);
        node.setPosition(240, 100);
        node2.setPosition(240, 400);
    }

    private float t = 0;

    @Override
    public void render(float delta) {
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        edge.draw(batch, delta);
        node.draw(batch, delta);
        node2.draw(batch, delta);
        t += delta;
        for (int i = 0; i < 10; i++)
            batch.draw(Tex.UNIT1.t, 240 - 10, 100 + (((t+i/10f)*100)%300) - 10, 20, 20);
        batch.end();
    }
}
