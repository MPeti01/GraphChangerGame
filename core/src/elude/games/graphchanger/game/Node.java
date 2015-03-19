package elude.games.graphchanger.game;

import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Peti on 2015.03.19..
 */
public class Node {

    public static final float RADIUS = 15f;

    private static float SPAWN_RELOAD = 2f;

    private List<Node> neighbors = new LinkedList<Node>();
    Player player = null;

    private float timeToSpawn = SPAWN_RELOAD;

    private final Vector2 pos;

    Node(Vector2 pos) {
        this.pos = pos;
    }

    void addNeighbor(Node neighbor) {
        neighbors.add(neighbor);
    }

    void removeNeighbor(Node neighbor) {
        neighbors.remove(neighbor);
    }

    void update(float delta) {
        if (player != null) {
            timeToSpawn -= delta;
            if (timeToSpawn < 0) {
                timeToSpawn += SPAWN_RELOAD;
                player.addUnit(this, destinationFromHere());
            }
        }
    }

    private int quickBadSolution = 0;
    public Node destinationFromHere() {
        quickBadSolution++;
        if (quickBadSolution == neighbors.size())
            quickBadSolution = 0;
        return neighbors.get(quickBadSolution);
    }

    public Player player() {
        return player;
    }

    public Vector2 pos() {
        return pos;
    }

    public void hitBy(Player owner) {
        player = owner;
    }
}
