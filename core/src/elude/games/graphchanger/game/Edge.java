package elude.games.graphchanger.game;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Peti on 2015.03.19..
 */
class Edge {

    private static final Vector2 v = new Vector2();

    public final Node node1;
    public final Node node2;

    public final Vector2 v1;
    public final Vector2 v2;
    public final float length;
    public final float angle;

    Edge(Node n1, Node n2) {
        node1 = n1;
        node2 = n2;
        v1 = n1.pos();
        v2 = n2.pos();
        angle = v.set(v2).sub(v1).angle();
        length = v1.dst(v2);
    }
}
