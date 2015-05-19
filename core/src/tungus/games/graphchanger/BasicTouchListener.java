package tungus.games.graphchanger;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Peti on 2015.03.19..
 */
public interface BasicTouchListener {
    public void onDown(Vector2 touch);
    public void onDrag(Vector2 touch);
    public void onUp(Vector2 touch);
}
