package tungus.games.graphchanger;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.game.GameScreen;

public class GraphChanger extends Game {

    public static Class<? extends Screen> mpScreen = null;

	@Override
	public void create () {
		Assets.load();
        if (Gdx.app.getType() != Application.ApplicationType.Android && mpScreen != null) {
            try {
                setScreen((Screen)(GraphChanger.mpScreen.getConstructors()[0].newInstance(this)));
            } catch (Exception e) {
                throw new GdxRuntimeException(e);
            }
        } else {
            setScreen(new GameScreen(this));
        }
    }
}
