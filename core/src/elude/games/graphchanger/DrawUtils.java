package elude.games.graphchanger;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Peti on 2015.03.22..
 */
public class DrawUtils {

    private static final Vector2 temp = new Vector2();

    public static void drawLine(SpriteBatch batch, Vector2 v1, float width, float length, float angle) {
        batch.draw(Assets.Tex.LINE.t, v1.x, v1.y-5f,     // Tex, pos
                0, 5f,                                   // Rotation point
                length, width,                             // Width, height
                1, 1,                                    // Scale
                angle);                                  // Rotation
    }

    public static void drawLine(SpriteBatch batch, Vector2 v1, Vector2 v2, float width) {
        temp.set(v2).sub(v1);
        drawLine(batch, v1, width, temp.len(), temp.angle());
    }
}
