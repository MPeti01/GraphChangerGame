package tungus.games.graphchanger;

import com.badlogic.gdx.math.Vector2;

/**
 * Simplified interface for handling the gist of touch events.
 */
public interface BasicTouchListener {
    public void onDown(Vector2 touch);
    public void onDrag(Vector2 touch);
    public void onUp(Vector2 touch);
}
