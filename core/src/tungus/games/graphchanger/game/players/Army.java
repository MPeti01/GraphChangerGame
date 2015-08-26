package tungus.games.graphchanger.game.players;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import tungus.games.graphchanger.game.graph.Destination;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.graph.node.Node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class containing and managing {@link Unit units} for one player
 */
public class Army {
    final List<Unit> units = new LinkedList<Unit>();
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

    public void addUnit(Node creator, Destination destination) {
        addUnit(creator.pos(), destination);
    }

    private void addUnit(Vector2 pos, Destination destination) {
        Unit u = unitPool.obtain();
        u.pos.set(pos);
        u.setDestination(destination);
        units.add(u);
    }

    public void updateUnits(float delta) {
        for (Iterator<Unit> it = units.iterator(); it.hasNext(); ) {
            Unit u = it.next();
            if (u.update(delta)) {
                it.remove();
            }
        }
    }

    public void renderUnits(SpriteBatch batch, float sinceTick) {
        for (Unit u : units) {
            u.render(batch, sinceTick);
        }
    }

    public Player player() {
        return player;
    }

    public void set(Army other, Graph graph) {
        for (Unit u : units) {
            unitPool.free(u);
        }
        units.clear();
        for (Unit u : other.units) {
            // Make sure to get the Node instance from the Graph handled with this Army
            Destination dest = u.getDestination().localCopy(graph);
            if (dest != null) {
                addUnit(u.pos, u.getDestination().localCopy(graph));
            } // else a PartialEdge was removed, the units on it die - no need to add them.

        }
    }

    public int id() {
        return player.ordinal();
    }
}
