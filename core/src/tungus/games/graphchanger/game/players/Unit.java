package tungus.games.graphchanger.game.players;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.Assets;
import tungus.games.graphchanger.game.graph.Destination;

import java.util.Random;

/**
 * A single unit moving independently on the map.
 */
class Unit {

    private static final float SIZE = 8f;
    public static float SPEED = 50f;

    private final Army owner;
    private Destination destination;
    final Vector2 pos;
    private final Vector2 vel;
    private final Vector2 idleRandomVel = new Vector2(1, 0);
    boolean reachedDest = false;
    private Random rand = new Random(123);

    public Unit(Army owner) {
        this.owner = owner;
        this.destination = null;
        this.pos = new Vector2();
        this.vel = new Vector2();
    }

    public void setDestination(Destination dest) {
        this.destination = dest;
        reachedDest = false;
    }

    public boolean update(float delta) {
        // Change the destination if needed
        Destination redirect = destination.remoteDestinationRedirect(owner.player());
        if (redirect == null) {
            return true;
        } else if (redirect != destination) {
            setDestination(redirect);
        }

        if (reachedDest) {
            setIdleMovementVel();
        } else {
            vel.set(destination.pos()).sub(pos).nor().scl(SPEED);
        }

        if (reachedDest || destination.isReachedAt(pos)) {
            Destination next = destination.nextDestinationForArrived(owner.player());
            if (next == null) {
                return true;
            } else if (next == destination) {
                reachedDest = true;
            } else {
                setDestination(next);
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

    private static final Vector2 tempPos = new Vector2();
    public void render(SpriteBatch batch, float sinceTick) {
        tempPos.set(pos);
        tempPos.add(vel.x * sinceTick, vel.y * sinceTick);
        batch.draw(Assets.Tex.UNITS[owner.id()].t, tempPos.x - SIZE / 2, tempPos.y - SIZE / 2, SIZE, SIZE);
    }
}
