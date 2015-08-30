package tungus.games.graphchanger;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.game.gamescreen.GameScreen;

import java.lang.reflect.Constructor;

public class GraphChanger extends Game {

    public static Class<? extends Screen> mpScreen = null;
    FPSLogger fps = new FPSLogger();

    private final String IP;
    private final int port;

    public GraphChanger() {
        IP = null;
        port = -1;
    }

    public GraphChanger(String IP, int port) {
        this.IP = IP;
        this.port = port;
    }

	@Override
	public void create () {
		Assets.load();
        if (Gdx.app.getType() != Application.ApplicationType.Android && mpScreen != null) {
            try {
                if (IP != null) {
                    Constructor<?>[] ctors = GraphChanger.mpScreen.getConstructors();
                    for (Constructor<?> ctor : ctors) {
                        if (ctor.getParameterTypes().length > 1) {
                            setScreen((Screen) ctor.newInstance(this, IP, port));
                        }
                    }
                } else {
                    Constructor<?>[] ctors = GraphChanger.mpScreen.getConstructors();
                    for (Constructor<?> ctor : ctors) {
                        if (ctor.getParameterTypes().length == 1) {
                            setScreen((Screen) ctor.newInstance(this));
                        }
                    }
                }
            } catch (Exception e) {
                throw new GdxRuntimeException(e);
            }
        } else {
            setScreen(new GameScreen(this));
        }
    }

    @Override
    public void render() {
        super.render();
        fps.log();
    }
}
