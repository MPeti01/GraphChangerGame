package tungus.games.graphchanger.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import tungus.games.graphchanger.GraphChanger;

@SuppressWarnings("WeakerAccess")
public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 480;
        config.height = 800;
		new LwjglApplication(new GraphChanger(), config);
    }
}
