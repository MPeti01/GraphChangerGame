package tungus.games.graphchanger;

import com.badlogic.gdx.Game;

public class GraphChanger extends Game {

	@Override
	public void create () {
		Assets.load();
		setScreen(new NetMPScreen(this));
	}
}
