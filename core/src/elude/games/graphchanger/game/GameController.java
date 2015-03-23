package elude.games.graphchanger.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import elude.games.graphchanger.BasicTouchListener;

/**
 * Created by Peti on 2015.03.19..
 */
public class GameController {
    private Graph graph;
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
