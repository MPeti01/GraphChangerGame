package tungus.games.graphchanger.game.players;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.Assets;
import tungus.games.graphchanger.game.graph.Node;

/**
 * One unit moving independently on the map.
 */
class Unit {

    private static final Vector2 temp = new Vector2();
    private static final float TIME_PER_EDGE = 1.3f;
    private static final float SIZE = 8f;
    private static final float SPEED = 100f;

    final Army owner;
    Node destination;
    final Vector2 pos;


    public Unit(Army owner) {
        this.owner = owner;
        this.destination = null;
        this.pos = new Vector2();
    }

    public void update(float delta) {
        if (pos.dst2(destination.pos()) < SPEED * SPEED * delta*delta) {
            pos.set(destination.pos());
            if (destination.player() == owner.player()) {
                destination.unitPassedBy();
                destination = destination.destinationFromHere();
            } else {
                destination.hitBy(owner);
                kill();
            }
        } else {
            temp.set(destination.pos()).sub(pos).nor().scl(SPEED *delta);
            pos.add(temp);
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(Assets.Tex.UNIT1.t, pos.x, pos.y, SIZE, SIZE);
    }

    private void kill() {
        owner.removeUnit(this);
    }
}
