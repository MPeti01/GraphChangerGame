package tungus.games.graphchanger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.menu.MainMenu;

public class GraphChanger extends Game {

    public static Class<? extends Screen> mpConnectScreen = null;
    private final FPSLogger fps = new FPSLogger();

	@Override
	public void create () {
		Assets.load();
        setScreen(new MainMenu(this));
    }

    @Override
    public void render() {
        super.render();
        fps.log();
    }

    public void setScreen(Class<? extends Screen> c) {
        try {
            setScreen(c.getConstructor(Game.class).newInstance(this));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GdxRuntimeException("Failed to instantiate screen from Class");
        }
    }

    @Override
    public void dispose() {
        screen.dispose();
    }
}
