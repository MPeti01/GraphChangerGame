package tungus.games.graphchanger.game.graph.node;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.Assets;
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

    public static final float RADIUS = 21.5f;

    final List<Node> neighbors = new LinkedList<Node>();

    private final List<Node> allNodes;

    private final Vector2 pos;
    private final CaptureHandler captureHandler;
    private final Upgrader upgrader;
    private final UnitSpawnController spawnCheck;

    /**
     * Stores the neighboring node(s) with the closest neutral/enemy nodes that way. Empty if none reachable.
     */
    final List<Node> primaryNeighbors = new LinkedList<Node>();
    private int nextDirectionIndex = 0;

    public final int id;

    public Node(Vector2 pos, int id, List<Node> allNodes) {
        this(null, pos, id, allNodes);
    }

    public Node(Player owner, Vector2 pos, int id, List<Node> allNodes) {
        this.pos = pos;
        this.id = id;
        this.allNodes = allNodes;

        upgrader = new Upgrader(owner, pos);
        spawnCheck = new UnitSpawnController(upgrader);
        captureHandler = new CaptureHandler(owner, pos, upgrader);
    }

    public Node(Node n, List<Node> allNodes) {
        this(n.player(), n.pos, n.id, allNodes);
    }

    public void update(float delta, Army... armies) {
        if (captureHandler.owner() != null) {
            spawnCheck.update(delta);
            if (spawnCheck.shouldSpawn()) {
                armies[captureHandler.owner().ordinal()].addUnit(this, destinationFromHere());
            }
        }
    }

    public Node destinationFromHere() {
        if (!primaryNeighbors.isEmpty())
            return nextDirection(primaryNeighbors);
        else
            return nextDirection(neighbors);
    }

    private Node nextDirection(List<Node> list) {
        if (list.size() == 0)
            return this;
        nextDirectionIndex++;
        if (nextDirectionIndex >= list.size())
            nextDirectionIndex = 0;
        return list.get(nextDirectionIndex);
    }

    /**
     * Notfies the Node that a unit passed it. Asks whether it should be removed.
     * @param passingPlayer The owner of the unit
     * @return Whether the unit should be removed (i.e. whether it is consumed for conquering/upgrading)
     */
    public boolean usesUnitPassingFrom(Player passingPlayer) {
        if (captureHandler.usesUnitPassingFrom(passingPlayer)) {
            return true;
        } else if (upgrader.usesUnitPassingFrom(passingPlayer)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean wouldUseUnitFrom(Player p) {
        return captureHandler.wouldUseUnitFrom(p) || upgrader.wouldUseUnitFrom(p);
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

    public Vector2 pos() {
        return pos;
    }

    public Player player() {
        return captureHandler.owner();
    }

    public void set(Node other) {
        spawnCheck.set(other.spawnCheck);
        captureHandler.set(other.captureHandler);
        upgrader.set(other.upgrader);
        nextDirectionIndex = other.nextDirectionIndex;

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

    public void render(SpriteBatch batch, boolean isSelected) {
        Assets.Tex tex = (player() == null ? Assets.Tex.NODE0 : Assets.Tex.NODES[player().ordinal()][upgrader.level]);
        if (isSelected) {
            tex = Assets.Tex.NODE_SELECTED;
        }
        batch.draw(tex.t, pos.x - Node.RADIUS, pos.y - Node.RADIUS, 2*Node.RADIUS, 2*Node.RADIUS);
        if (captureHandler.isUnderAttack())
            captureHandler.renderBar(batch);
        if (upgrader.upgrading()) {
            upgrader.render(batch);
        }
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Node && ((Node)o).id == id);
    }

    public void upgrade() {
        upgrader.startUpgrade();
    }
}
