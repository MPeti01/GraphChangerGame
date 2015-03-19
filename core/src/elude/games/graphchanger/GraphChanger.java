package elude.games.graphchanger;

import com.badlogic.gdx.Game;
import elude.games.graphchanger.game.GameScreen;

public class GraphChanger extends Game {

	@Override
	public void create () {
        Assets.load();
		setScreen(new GameScreen(this));
	}

}
