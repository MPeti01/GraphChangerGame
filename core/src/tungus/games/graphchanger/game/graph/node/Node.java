package tungus.games.graphchanger.game.graph.node;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.graph.*;
import tungus.games.graphchanger.game.players.Army;
import tungus.games.graphchanger.game.players.Player;
import tungus.games.graphchanger.game.render.NodeEffect;

import java.util.List;

/**
 * A node in the graph to be conquered by players. Handles/delegates neighbor relations, spawning
 * {@link tungus.games.graphchanger.game.players.Unit units} and giving direction to passing units.
 */
public class Node implements Destination {

    public static final float RADIUS = 21.5f;

    private final List<Node> allNodes;

    private final Vector2 pos;
    private final CaptureHandler captureHandler;
    private final Upgrader upgrader;
    private final UnitSpawnController spawnCheck;
    private final EdgeHandler edges;

    public final int id;

    private final NodeEffect effect;

    public Node(Player owner, Vector2 pos, int level, int id,
                List<Node> allNodes, List<Edge> allEdges, EdgePricer pricer, List<PartialEdge> partialEdges) {
        this.pos = pos;
        this.id = id;
        upgrader = new Upgrader(owner, pos, level);
        spawnCheck = new UnitSpawnController(upgrader);
        captureHandler = new CaptureHandler(owner, pos, upgrader);
        edges = new EdgeHandler(this, allEdges, pricer, allNodes, partialEdges);
        this.allNodes = allNodes;
        effect = new NodeEffect(pos, owner, level);
    }

    public Node(Vector2 pos, int id,
                List<Node> allNodes, List<Edge> allEdges, EdgePricer pricer, List<PartialEdge> partialEdges) {
        this(null, pos, 0, id, allNodes, allEdges, pricer, partialEdges);
    }

    public Node(Node n, List<Node> allNodes, List<Edge> allEdges, EdgePricer pricer, List<PartialEdge> partialEdges) {
        this(n.player(), n.pos, n.upgrader.level, n.id, allNodes, allEdges, pricer, partialEdges);
    }

    public void update(float delta, Army... armies) {
        if (captureHandler.owner() != null) {
            spawnCheck.update(delta);
            if (spawnCheck.shouldSpawn()) {
                Destination dest = nextDestinationForArrived(captureHandler.owner());
                if (dest != null) { // Unit not consumed on this node
                    armies[captureHandler.owner().ordinal()].addUnit(this, dest);
                }
            }
        }
    }

    @Override
    public boolean isReachedAt(Vector2 unitPos) {
        return unitPos.dst2(pos) < 16;
    }

    /**
     * Notfies the Node that a unit passed it. Returns what should happen to it.
     * @param passingPlayer The owner of the unit
     * @return The next destination for the unit if this Node cannot consume it, null if it can and did.
     */
    public Destination nextDestinationForArrived(Player passingPlayer) {
        if (captureHandler.usesUnitPassingFrom(passingPlayer)) {
            if (captureHandler.justCaptured()) {
                edges.clearOutNeighbors();
                effect.setColorForPlayer(player());
            }
            return null;
        }
        Destination contestedEdge = edges.contestedEdge();
        if (contestedEdge != null) {
            return contestedEdge;
        }
        if (upgrader.usesUnitPassingFrom(passingPlayer)) {
            if (upgrader.justUpgraded())
                effect.incrementLevel();
            return null;
        }

        return edges.destinationFromHere();
    }

    @Override
    public Destination remoteDestinationRedirect(Player owner) {
        return this;
    }

    @Override
    public Destination localCopy(Graph g) {
        return g.nodes.get(id);
    }

    public boolean wouldUseUnitFrom(Player p) {
        return captureHandler.wouldUseUnitFrom(p) || upgrader.wouldUseUnitFrom(p) || edges.wouldUseUnitFrom(p);
    }

    public void removeEdgeTo(Node other) {
        edges.removeEdgeTo(other);
    }

    /**
     * Starts building an Edge to the given Node
     */
    public void buildEdgeTo(Node other) {
        edges.startEdgeTo(other);
    }

    /**
     * Instantly adds an Edge connecting it to the given Node
     */
    public Edge addEdgeTo(Node other) {
        return edges.addEdgeTo(other);
    }

    void addEdgeFrom(Node other) {
        edges.addEdgeFrom(other);
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

    void clearPrimaryNeighbors() {
        edges.primaryNeighbors.clear();
    }

    void addPrimaryNeighbor(Node node) {
        edges.addPrimaryNeighbor(node);
    }

    public void render(SpriteBatch batch, boolean isSelected, float delta) {
        /*Assets.Tex tex = (player() == null ? Assets.Tex.NODE0 : Assets.Tex.NODES[player().ordinal()][upgrader.level]);
        if (isSelected) {
            tex = Assets.Tex.NODE_SELECTED;
        }
        batch.draw(tex.t, pos.x - Node.RADIUS, pos.y - Node.RADIUS, 2*Node.RADIUS, 2*Node.RADIUS);*/
        effect.draw(batch, delta);
        captureHandler.render(batch);
        upgrader.render(batch, delta);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Node && ((Node)o).id == id);
    }

    public void upgrade() {
        upgrader.startUpgrade();
    }

    /**
     * Notifies the Node that an Edge is being built towards it. Used to synchronize competing edges being built
     * in opposite directions
     * @param source The Node the edge is being built from
     * @param progress How much of the edge is completed
     */
    public void reachingWithEdge(Node source, float progress) {
        edges.reachingWithEdge(source, progress);
    }

    boolean isContesting(Node neighbor) {
        return edges.isContesting(neighbor);
    }

    public void set(Node other) {
        spawnCheck.set(other.spawnCheck);
        captureHandler.set(other.captureHandler);
        upgrader.set(other.upgrader);
        edges.set(other.edges, allNodes);
    }
}
