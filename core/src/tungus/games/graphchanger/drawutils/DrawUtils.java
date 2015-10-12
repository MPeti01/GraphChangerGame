package tungus.games.graphchanger.drawutils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.Assets.Tex;

/**
 * Routines related to rendering
 */
public class DrawUtils {

    private static final Vector2 temp = new Vector2();

    public static void drawLine(SpriteBatch batch, TextureRegion tex, Vector2 v1, float width, float length, float angle) {
        //noinspection SuspiciousNameCombination
        batch.draw(tex, v1.x, v1.y - width / 2,     // Tex, pos
                0, width / 2,                                   // Rotation point
                length, width,                             // Width, height
                1, 1,                                    // Scale
                angle);                                  // Rotation
    }

    public static void drawLine(SpriteBatch batch, Vector2 v1, float width, float length, float angle) {
        drawLine(batch, Tex.LINE.t, v1, width, length, angle);
    }

    /**
     * NOTE: Prefer the other versions if possible, they run much quicker (no atan() required)
     */
    public static void drawLine(SpriteBatch batch, Vector2 v1, Vector2 v2, float width) {
        temp.set(v2).sub(v1);
        drawLine(batch, v1, width, temp.len(), temp.angle());
    }
}
