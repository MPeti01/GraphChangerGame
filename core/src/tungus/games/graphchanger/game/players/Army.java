package tungus.games.graphchanger.game.players;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import tungus.games.graphchanger.game.graph.Node;
import tungus.games.graphchanger.game.graph.NodeList;

import java.util.LinkedList;
import java.util.List;

/**
 * Class containing and managing units for one player
 */
public class Army {
    private final List<Unit> units = new LinkedList<Unit>();
    private final Player player;
    private final Pool<Unit> unitPool = new Pool<Unit>() {
        @Override
        protected Unit newObject() {
            return new Unit(Army.this);
        }
    };

    public Army(Player player) {
        this.player = player;
    }

    public void addUnit(Node creator, Node destination) {
        addUnit(creator.pos(), destination);
    }

    private void addUnit(Vector2 pos, Node destination) {
        Unit u = unitPool.obtain();
        u.destination = destination;
        u.pos.set(pos);
        units.add(u);
    }

    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    public void updateUnits(float delta) {
        // Units might call to remove themselves from update, so iterate backwards to avoid messing with indices
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

    public void set(Army other, NodeList nodes) {
        for (Unit u : units) {
            unitPool.free(u);
        }
        units.clear();
        for (Unit u : other.units) {
            // Make sure to get the Node instance from the Graph handled with this Army
            addUnit(u.pos, nodes.get(u.destination.id));
        }
    }

    public int id() {
        return player.ordinal();
    }
}
