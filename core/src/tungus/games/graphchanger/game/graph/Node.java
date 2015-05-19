package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.players.Army;
import tungus.games.graphchanger.game.players.Player;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Peti on 2015.03.19..
 */
public class Node {

    public static final float RADIUS = 15f;

    final List<Node> neighbors = new LinkedList<Node>();

    private final NodeList allNodes;

    private Army owner = null;
    private final Vector2 pos;
    private final UnitSpawnController spawnCheck = new UnitSpawnController();

    public final int id;

    Node(Vector2 pos, int id, NodeList allNodes) {
        this(null, pos, id, allNodes);
    }

    Node(Army owner, Vector2 pos, int id, NodeList allNodes) {
        this.pos = pos;
        this.owner = owner;
        this.id = id;
        this.allNodes = allNodes;
    }

    void addNeighbor(int neighborID) {
        addNeighbor(allNodes.get(neighborID));
    }

    void addNeighbor(Node newNeighbor) {
        if (!neighbors.contains(newNeighbor)) {
            neighbors.add(newNeighbor);
        }
    }

    void removeNeighbor(Node neighbor) {
        neighbors.remove(neighbor);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean hasNeighbor(Node other) {
        return neighbors.contains(other);
    }

    void update(float delta) {
        if (owner != null) {
            spawnCheck.update(delta);
            if (spawnCheck.shouldSpawn()) {
                owner.addUnit(this, destinationFromHere());
            }
        }
    }

    //TODO Implement proper unit movement logic
    private int quickBadSolution = 0;
    public Node destinationFromHere() {
        if (neighbors.size() == 0)
            return this;
        quickBadSolution++;
        if (quickBadSolution >= neighbors.size())
            quickBadSolution = 0;
        return neighbors.get(quickBadSolution);
    }

    public void unitPassedBy() {
        spawnCheck.unitPassedBy();
    }

    public Player player() {
        return owner == null ? null : owner.player();
    }

    public Vector2 pos() {
        return pos;
    }

    public void hitBy(Army army) {
        this.owner = army;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Node && ((Node)o).id == id);
    }

    public void set(Node other) {
        //TODO Investigate setting of spawnChecker..
        Iterator<Node> it = neighbors.iterator();
        while (it.hasNext()) {
            Node neighbor = it.next();
            if (!other.neighbors.contains(neighbor)) {
                it.remove();
            }
        }
        for (Node n : other.neighbors) {
            if (!neighbors.contains(n)) {
                neighbors.add(allNodes.get(n.id));
            }
        }
    }
}
