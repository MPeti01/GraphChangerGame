package tungus.games.graphchanger;

import com.badlogic.gdx.Game;
import tungus.games.graphchanger.game.GameScreen;

public class GraphChanger extends Game {

	@Override
	public void create () {
        Assets.load();
		setScreen(new GameScreen(this));
	}

}
