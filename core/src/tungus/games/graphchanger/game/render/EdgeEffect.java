package tungus.games.graphchanger.game.render;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.Assets;
import tungus.games.graphchanger.drawutils.DistanceParticleEmitter;
import tungus.games.graphchanger.game.players.Player;

/**
 * Extended ParticleEffect for rendering Edges. Uses DistanceParticleEmitters.
 */
public class EdgeEffect extends ParticleEffect {

    private final float maxLength;

    public EdgeEffect(Player p, Vector2 start, float angle, float len, float progress) {
        super();
        maxLength = len;

        DistanceParticleEmitter emitter = new DistanceParticleEmitter((DistanceParticleEmitter) Assets.edgeEmitter);
        emitter.getTint().setColors(new float[]{p.edgeColor.r, p.edgeColor.g, p.edgeColor.b});
        emitter.setDistance(len * progress);
        emitter.getAngle().setLow(angle);
        emitter.getRotation().setLow(angle);

        getEmitters().add(emitter);
        setEmittersCleanUpBlendFunction(false);
        loadEmitterImages(Assets.atlas);
        setPosition(start.x, start.y);
    }

    public void setProgress(float progress) {
        ((DistanceParticleEmitter)getEmitters().get(0)).setDistance(progress*maxLength);
    }
}
