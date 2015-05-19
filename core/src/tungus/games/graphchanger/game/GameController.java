package tungus.games.graphchanger.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tungus.games.graphchanger.BasicTouchListener;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.players.Army;
import tungus.games.graphchanger.game.players.Player;

/**
 * Created by Peti on 2015.03.19..
 */
class GameController {
    private final Graph graph;
    private final Army[] armies = new Army[Player.values().length];

    public GameController() {
        for (int i = 0; i < armies.length; i++) {
            armies[i] = new Army(Player.values()[i]);
        }
        graph = new Graph("test.txt", armies);
    }
    public void render(float delta, SpriteBatch batch) {
        graph.updateNodes(delta);
        for (Army a : armies) {
            a.updateUnits(delta);
        }

        graph.render(batch);
        for (Army a : armies) {
            a.renderUnits(batch);
        }
    }

    public BasicTouchListener getTouchListener() {
        return graph.getEditorInput();
    }
}
