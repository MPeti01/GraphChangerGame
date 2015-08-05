package tungus.games.graphchanger.game.graph.node;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.Assets;
import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.players.Army;
import tungus.games.graphchanger.game.players.Player;

import java.util.List;

/**
 * A node in the graph to be conquered by players. Handles/delegates neighbor relations, spawning
 * {@link tungus.games.graphchanger.game.players.Unit units} and giving direction to passing units.
 */
public class Node {

    public static final float RADIUS = 21.5f;

    private final List<Node> allNodes;

    private final Vector2 pos;
    private final CaptureHandler captureHandler;
    private final Upgrader upgrader;
    private final UnitSpawnController spawnCheck;
    private final EdgeHandler edges;

    public final int id;

    public Node(Player owner, Vector2 pos, int id, List<Node> allNodes, List<Edge> allEdges) {
        this.pos = pos;
        this.id = id;
        this.allNodes = allNodes;

        upgrader = new Upgrader(owner, pos);
        spawnCheck = new UnitSpawnController(upgrader);
        captureHandler = new CaptureHandler(owner, pos, upgrader);
        edges = new EdgeHandler(this, allEdges);
    }

    public Node(Vector2 pos, int id, List<Node> allNodes, List<Edge> allEdges) {
        this(null, pos, id, allNodes, allEdges);
    }

    public Node(Node n, List<Node> allNodes, List<Edge> allEdges) {
        this(n.player(), n.pos, n.id, allNodes, allEdges);
    }

    public void update(float delta, Army... armies) {
        if (captureHandler.owner() != null) {
            spawnCheck.update(delta);
            if (spawnCheck.shouldSpawn()) {
                Node dest = nextDestinationFor(captureHandler.owner());
                if (dest != null) { // Unit not consumed on this node
                    armies[captureHandler.owner().ordinal()].addUnit(this, dest);
                }
            }
        }
    }

    /**
     * Notfies the Node that a unit passed it. Returns what should happen to it.
     * @param passingPlayer The owner of the unit
     * @return The next destination for the unit if this Node cannot consume it, null if it can and did.
     */
    public Node nextDestinationFor(Player passingPlayer) {
        if (captureHandler.usesUnitPassingFrom(passingPlayer)) {
            if (captureHandler.justCaptured()) {
                edges.clearOutNeighbors();
            }
            return null;
        } else if (upgrader.usesUnitPassingFrom(passingPlayer)) {
            return null;
        } else if (edges.usesPassingUnitFrom(passingPlayer)) {
            return null;
        } else {
            return edges.destinationFromHere();
        }
    }

    public boolean wouldUseUnitFrom(Player p) {
        return captureHandler.wouldUseUnitFrom(p) || upgrader.wouldUseUnitFrom(p) || edges.wouldUseUnitFrom(p);
    }

    public void addEdgeTo(Node other) {
        edges.startEdgeTo(other);
    }

    void addEdgeFrom(Node other) {
        edges.addEdgeFrom(other);
    }

    public void removeEdgeTo(Node other) {
        edges.removeEdgeTo(other);
    }

    void removeEdgeFrom(Node other) {
        edges.removeEdgeFrom(other);
    }

    public Vector2 pos() {
        return pos;
    }

    public Player player() {
        return captureHandler.owner();
    }

    public List<Node> outNeighbors() {
        return edges.outNeighbors;
    }

    public List<Node> inNeighbors() {
        return edges.inNeighbors;
    }

    public List<Node> primaryNeighbors() {
        return edges.primaryNeighbors;
    }

    public void set(Node other) {
        spawnCheck.set(other.spawnCheck);
        captureHandler.set(other.captureHandler);
        upgrader.set(other.upgrader);
        edges.set(other.edges, allNodes);
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
