package tungus.games.graphchanger.game.players;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.Assets.Tex;
import tungus.games.graphchanger.game.graph.Destination;

import java.util.Random;

/**
 * A single unit moving independently on the map.
 */
class Unit {

    private static final float SIZE = 8f;
    public static float SPEED = 50f;

    private final Player player;
    private final TextureRegion tex;
    private Destination destination;
    final Vector2 pos;
    private final Vector2 vel;
    private final Vector2 idleRandomVel = new Vector2(1, 0);
    boolean reachedDest = false;
    private final Vector2 lastDestPos = new Vector2();
    private Random rand = new Random(123);

    public Unit(Army owner) {
        this.player = owner.player();
        this.destination = null;
        this.pos = new Vector2();
        this.vel = new Vector2();
        tex = Tex.UNITS[owner.id()].t;
    }

    public void setDestination(Destination dest) {
        this.destination = dest;
        reachedDest = false;
        vel.set(destination.pos()).sub(pos).nor().scl(SPEED);
        lastDestPos.set(dest.pos());
    }

    public boolean update(float delta) {
        // Change the destination if needed
        Destination redirect = destination.remoteDestinationRedirect(player);
        if (redirect != destination) {
            if (redirect == null)
                return true;
            setDestination(redirect);
        }

        if (reachedDest) {
            setIdleMovementVel();
        } else if (destination.pos().x != lastDestPos.x || destination.pos().y != lastDestPos.y) {
            // Float equality is fine here, because we set lastDestPos from this value
            vel.set(destination.pos()).sub(pos).nor().scl(SPEED);
            lastDestPos.set(destination.pos());
        }

        if (reachedDest || destination.isReachedAt(pos)) {
            Destination next = destination.nextDestinationForArrived(player);
            if (next == null) {
                return true;
            } else if (next == destination) {
                reachedDest = true;
            } else {
                setDestination(next);
                return false; // Don't update the position in this tick, because we might go past the destination
            }
        }

        pos.add(vel.x * delta, vel.y * delta);
        return false;
    }

    private void setIdleMovementVel() {
        rand.setSeed((long) pos.x);
        idleRandomVel.rotate(rand.nextFloat() * 360);
        vel.set(pos).sub(destination.pos()).scl(-1 / 20f).add(idleRandomVel).scl(SPEED);
    }

    public void render(SpriteBatch batch, float sinceTick) {
        batch.draw(tex, pos.x + vel.x * sinceTick - SIZE / 2,
                pos.y + vel.y * sinceTick - SIZE / 2, SIZE, SIZE);
    }
}
