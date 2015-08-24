package tungus.games.graphchanger.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * An InputProcessor that wraps a BasicTouchListener.
 * Also capable of applying a Camera's unprojection if given one.
 */
public class BasicTouchWrapper extends InputMultiplexer {

    private Camera cam = null;
    private final Vector3 touch3 = new Vector3();
    private final Vector2 touch2 = new Vector2();

    private final BasicTouchListener listener;

    public BasicTouchWrapper(BasicTouchListener l) {
        super();
        listener = l;
        addProcessor(basicInput);
        addProcessor(new GestureDetector(gestureInput));
    }

    private final InputProcessor basicInput = new InputAdapter() {
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            listener.onDown(unproject(screenX, screenY));
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            listener.onUp(unproject(screenX, screenY));
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            listener.onDrag(unproject(screenX, screenY));
            return false;
        }
    };

    private final GestureDetector.GestureListener gestureInput = new GestureDetector.GestureAdapter() {
        @Override
        public boolean tap(float x, float y, int count, int button) {
            if (count == 2) {
                listener.doubleTap(unproject(x, y));
            }
            return false;
        }
    };


    private Vector2 unproject(float x, float y) {
        touch3.set(x, y, 0);
        if (cam != null)
            cam.unproject(touch3);
        touch2.set(touch3.x, touch3.y);
        return touch2;
    }

    public void setCamera(Camera c) {
        cam = c;
    }
}
