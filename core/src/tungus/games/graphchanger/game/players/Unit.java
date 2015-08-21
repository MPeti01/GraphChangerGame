package tungus.games.graphchanger.game.players;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.Assets;
import tungus.games.graphchanger.game.graph.Destination;

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


    public Unit(Army owner) {
        this.owner = owner;
        this.destination = null;
        this.pos = new Vector2();
        this.vel = new Vector2();
    }

    public void setDestination(Destination dest) {
        this.destination = dest;
        vel.set(dest.pos()).sub(pos).nor().scl(SPEED);
    }

    public Destination getDestination() {
        return destination;
    }

    public void update(float delta) {
        if (destination.isReachedAt(pos)) {
            Destination next = destination.nextDestinationFor(owner.player());
            if (next == null) {
                kill();
            } else {
                setDestination(next);
            }
        } else {
            pos.add(vel.x * delta, vel.y * delta);
        }
    }

    private static final Vector2 tempPos = new Vector2();
    public void render(SpriteBatch batch, float sinceTick) {
        tempPos.set(pos);
        tempPos.add(vel.x * sinceTick, vel.y * sinceTick);
        batch.draw(Assets.Tex.UNITS[owner.id()].t, tempPos.x - SIZE / 2, tempPos.y - SIZE / 2, SIZE, SIZE);
    }

    void kill() {
        owner.removeUnit(this);
    }
}
