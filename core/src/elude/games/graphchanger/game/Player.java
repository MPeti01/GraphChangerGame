package elude.games.graphchanger.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.LinkedList;
import java.util.List;

public enum Player {
    P1, P2;

    private final List<Unit> units = new LinkedList<Unit>();

    public void addUnit(Node creator, Node destination) {
        units.add(new Unit(this, creator.pos().cpy(), destination));
    }

    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    public void updateUnits(float delta) {
        /*for (Unit u : units) {
            u.update(delta);
        }*/
        for (int i = units.size()-1; i >= 0; i--) {
            units.get(i).update(delta);
        }
    }

    public void renderUnits(SpriteBatch batch) {
        for (Unit u : units) {
            u.render(batch);
        }
    }
}
