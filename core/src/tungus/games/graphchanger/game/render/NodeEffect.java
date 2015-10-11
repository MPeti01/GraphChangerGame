package tungus.games.graphchanger.game.render;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.Assets;
import tungus.games.graphchanger.game.players.Player;

/**
 * Extended ParticleEffect for rendering Nodes.
 */
public class NodeEffect extends ParticleEffect {
    public NodeEffect(Vector2 pos, Player player) {
        super(Assets.node.obtain());
        setPosition(pos.x, pos.y);
        setColorForPlayer(player);
        // Don't take time for fading in. Effects slightly fade every 10s, don't have that in sync.
        //     update(5f + MathUtils.random()*10f);
        // And don't use one update call, because all particles' lifetime would be even more in sync.
        int c = 20 + MathUtils.random(40);
        for (int i = 0; i < c; i++) {
            update(0.25f);
        }
        setEmittersCleanUpBlendFunction(false);
    }

    public void setColorForPlayer(Player p) {
        float[] color = (p == null ? new float[]{1, 1, 1} :
                                     new float[]{p.mainColor.r, p.mainColor.g, p.mainColor.b});
        for (ParticleEmitter e : getEmitters()) {
            e.getTint().setColors(color);
        }
    }
}
