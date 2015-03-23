package elude.games.graphchanger.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Peti on 2015.03.23..
 */
public class Army {
    private final List<Unit> units = new LinkedList<Unit>();
    private final Player player;

    public Army(Player player) {
        this.player = player;
    }

    public void addUnit(Node creator, Node destination) {
        units.add(new Unit(this, creator.pos().cpy(), destination));
    }

    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    public void updateUnits(float delta) {
        for (int i = units.size()-1; i >= 0; i--) {
            units.get(i).update(delta);
        }
    }

    public void renderUnits(SpriteBatch batch) {
        for (Unit u : units) {
            u.render(batch);
        }
    }

    public Player player() {
        return player;
    }
}
