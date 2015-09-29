package tungus.games.graphchanger.game.render;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.MathUtils;
import tungus.games.graphchanger.Assets;
import tungus.games.graphchanger.game.players.Player;

/**
 * Extended ParticleEffect for rendering Nodes.
 */
public class NodeEffect extends ParticleEffect {
    public NodeEffect() {
        super(Assets.node.obtain());
        // Don't take time for fading in. Also, effects slightly fade every 10s, don't have that in sync.
        update(5f + MathUtils.random()*10f);
        setEmittersCleanUpBlendFunction(false);
    }

    public void setColorForPlayer(Player p) {
        for (ParticleEmitter e : getEmitters()) {
            e.getTint().setColors(new float[]{p.mainColor.r, p.mainColor.g, p.mainColor.b});
        }
    }
}
