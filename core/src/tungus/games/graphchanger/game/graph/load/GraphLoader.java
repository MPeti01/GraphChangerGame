package tungus.games.graphchanger.game.graph.load;

import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.graph.EdgePricer;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.graph.PartialEdge;
import tungus.games.graphchanger.game.graph.node.Node;
import tungus.games.graphchanger.game.players.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Creates a Graph. This abstract class provides methods to make creating Nodes/Edges
 * and creating many separate Graph instances easier. Subclasses actually fill the Node
 * and Edge lists.
 */
public abstract class GraphLoader {

    public static enum Mode {
        LOAD_FILE, PERF_TEST, RANDOM_EMPTY, RANDOM_MIXED
    }

    public List<Node> nodes = new ArrayList<Node>();
    public List<Edge> edges = new LinkedList<Edge>();
    public List<PartialEdge> partialEdges = new LinkedList<PartialEdge>();

    /**
     * Loads or creates the data needed for the Graph.
     */
    public abstract void load();

    protected Node newNode(Vector2 pos) {
        return newNode(pos, null, 0);
    }

    protected Node newNode(Vector2 pos, Player player, int level) {
        /* The null EdgePricer is okay, because we duplicate the Nodes with an existing
        EdgePricer before creating a Graph from the lists anyway. */
        Node n = new Node(player, pos, level, nodes.size(), nodes, edges, null, partialEdges);
        nodes.add(n);
        return n;
    }

    protected void addEdge(Node n1, Node n2) {
        Edge e = n1.addEdgeTo(n2);
        e.createEffect();
    }

    protected void addEdge(int id1, int id2) {
        addEdge(nodes.get(id1), nodes.get(id2));
    }


    /**
     * Creates new Lists, Nodes and Edges so that separate Graph instances will not
     * reference the same compositing objects.
     */
    private void duplicate(EdgePricer pricer) {
        List<Node> newNodes = new ArrayList<Node>();
        List<Edge> newEdges = new LinkedList<Edge>();
        partialEdges = new LinkedList<PartialEdge>();
        for (Node n : nodes) { // Create the new Nodes
            newNodes.add(new Node(n, newNodes, newEdges, pricer, partialEdges));
        }
        for (int i = 0; i < nodes.size(); i++) { // Set up the same connections
            newNodes.get(i).set(nodes.get(i));
        }
        for (Edge e : newEdges) { // Edges just created by Node setting, except for effects
            e.createEffect();
        }
        nodes = newNodes;
        edges = newEdges;
    }

    public Graph createGraph(EdgePricer pricer) {
        if (nodes.isEmpty()) throw new IllegalStateException("Graph not loaded yet!");
        duplicate(pricer);
        return new Graph(nodes, edges, partialEdges);
    }
}
