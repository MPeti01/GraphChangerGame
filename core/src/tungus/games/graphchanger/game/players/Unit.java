package tungus.games.graphchanger.game.players;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.Assets;
import tungus.games.graphchanger.game.graph.Node;

/**
 * A single unit moving independently on the map.
 */
class Unit {

    private static final float SIZE = 8f;
    private static final float SPEED = 50f;

    private final Army owner;
    private Node destination;
    final Vector2 pos;
    private final Vector2 vel;


    public Unit(Army owner) {
        this.owner = owner;
        this.destination = null;
        this.pos = new Vector2();
        this.vel = new Vector2();
    }

    public void setDestination(Node dest) {
        this.destination = dest;
        vel.set(dest.pos()).sub(pos).nor().scl(SPEED);
    }

    public Node getDestination() {
        return destination;
    }

    public void update(float delta) {
        if (pos.dst2(destination.pos()) < SPEED * SPEED * delta*delta) {
            pos.set(destination.pos());
            if (destination.player() == owner.player()) {
                destination.unitPassedBy();
                setDestination(destination.destinationFromHere());
            } else {
                destination.hitBy(owner);
                kill();
            }
        } else {
            pos.add(vel.x * delta, vel.y * delta);
        }
    }

    private static final Vector2 temppos = new Vector2();
    public void render(SpriteBatch batch, float sinceTick) {
        temppos.set(pos);
        temppos.add(vel.x * sinceTick, vel.y * sinceTick);
        batch.draw(Assets.Tex.UNITS[owner.id()].t, temppos.x - SIZE / 2, temppos.y - SIZE / 2, SIZE, SIZE);
    }

    void kill() {
        owner.removeUnit(this);
    }
}
