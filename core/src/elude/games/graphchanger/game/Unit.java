package elude.games.graphchanger.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import elude.games.graphchanger.Assets;

/**
 * Created by Peti on 2015.03.19..
 */
public class Unit {

    private static final Vector2 temp = new Vector2();
    private static final float TIME_PER_EDGE = 1.3f;
    private static final float SIZE = 8f;

    private final Army owner;
    private Node destination;
    private Vector2 pos;
    private float speed;

    public Unit(Army owner, Vector2 pos, Node destination) {
        this.owner = owner;
        this.destination = destination;
        this.pos = pos;
        calcSpeed();
    }

    /**
     * Sets the speed to reach the destination in TIME_PER_EDGE seconds.
     */
    private void calcSpeed() {
        speed = pos.dst(destination.pos()) / TIME_PER_EDGE;
    }

    public void update(float delta) {
        if (pos.dst2(destination.pos()) < speed*speed * delta*delta) {
            pos.set(destination.pos());
            if (destination.player() == owner.player()) {
                destination = destination.destinationFromHere();
                calcSpeed();
            } else {
                destination.hitBy(owner);
                kill();
            }
        } else {
            temp.set(destination.pos()).sub(pos).nor().scl(speed*delta);
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
