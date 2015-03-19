package elude.games.graphchanger.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import elude.games.graphchanger.BasicTouchListener;

/**
 * Created by Peti on 2015.03.19..
 */
public class GameController {
    private Graph graph = new Graph("test.txt");

    public void render(float delta, SpriteBatch batch) {
        graph.updateNodes(delta);
        for (Player p : Player.values()) {
            p.updateUnits(delta);
        }

        graph.render(batch);
        for (Player p : Player.values()) {
            p.renderUnits(batch);
        }
    }

    public BasicTouchListener getTouchListener() {
        return graph.getEditorInput();
    }
}
