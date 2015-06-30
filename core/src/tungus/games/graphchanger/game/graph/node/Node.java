package tungus.games.graphchanger.game.graph.node;

import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.players.Army;
import tungus.games.graphchanger.game.players.Player;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A node in the graph to be conquered by players. Handles/delegates neighbor relations, spawning
 * {@link tungus.games.graphchanger.game.players.Unit units} and giving direction to passing units.
 */
public class Node {

    public static final float RADIUS = 15f;

    final List<Node> neighbors = new LinkedList<Node>();

    private final List<Node> allNodes;

    private Army owner = null;
    private final Vector2 pos;
    private final UnitSpawnController spawnCheck = new UnitSpawnController();

    /**
     * Stores the neighboring node(s) with the closest neutral/enemy nodes that way. Empty if none reachable.
     */
    List<Node> primaryNeighbors = new LinkedList<Node>();
    private int nextDirectionIndex = 0;

    public final int id;

    public Node(Vector2 pos, int id, List<Node> allNodes) {
        this(null, pos, id, allNodes);
    }

    public Node(Army owner, Vector2 pos, int id, List<Node> allNodes) {
        this.pos = pos;
        this.owner = owner;
        this.id = id;
        this.allNodes = allNodes;
    }

    public void update(float delta) {
        if (owner != null) {
            spawnCheck.update(delta);
            if (spawnCheck.shouldSpawn()) {
                owner.addUnit(this, destinationFromHere());
            }
        }
    }

    public Node destinationFromHere() {
        if (!primaryNeighbors.isEmpty())
            return nextDirection(primaryNeighbors);
        else
            return nextDirection(neighbors);
    }

    public Node nextDirection(List<Node> list) {
        if (list.size() == 0)
            return this;
        nextDirectionIndex++;
        if (nextDirectionIndex >= list.size())
            nextDirectionIndex = 0;
        return list.get(nextDirectionIndex);
    }

    /**
     * Notfies the Node that a unit passed it. Asks whether it should be removed.
     * @param passingArmy The owner of the unit
     * @return Whether the unit should be removed (i.e. whether it is consumed for conquering/upgrading)
     */
    public boolean removeUnitPassedBy(Army passingArmy) {
        if (passingArmy == owner) {
            spawnCheck.unitPassedBy();
            return false;
        } else {
            this.owner = passingArmy;
            return true;
        }
    }

    public void addNeighbor(int neighborID) {
        addNeighbor(allNodes.get(neighborID));
    }

    public void addNeighbor(Node newNeighbor) {
        if (!neighbors.contains(newNeighbor)) {
            neighbors.add(newNeighbor);
        }
    }

    public void removeNeighbor(Node neighbor) {
        neighbors.remove(neighbor);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasNeighbor(Node other) {
        return neighbors.contains(other);
    }

    public boolean hasNeighbor(int otherID) {
        for (Node o : neighbors)
            if (o.id == otherID) return true;
        return false;
    }

    public Player player() {
        return owner == null ? null : owner.player();
    }

    public Vector2 pos() {
        return pos;
    }

    public void set(Node other, Army... armies) {
        spawnCheck.set(other.spawnCheck);
        nextDirectionIndex = other.nextDirectionIndex;
        if (other.owner == null)
            owner = null;
        else
            owner = armies[other.owner.id()];
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

    @Override
    public boolean equals(Object o) {
        return (o instanceof Node && ((Node)o).id == id);
    }

    public void upgrade() {
        // TODO Upgrade node
    }
}
