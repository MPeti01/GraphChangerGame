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

    public List<Node> nodes = new ArrayList<Node>();
    public List<Edge> edges = new LinkedList<Edge>();
    public List<PartialEdge> partialEdges = new LinkedList<PartialEdge>();

    /**
     * Loads or creates the data needed for the Graph.
     */
    public abstract void load();

    protected void newNode(Vector2 pos) {
        newNode(pos, null);
    }

    protected void newNode(Vector2 pos, Player player) {
        /* The null EdgePricer is okay, because we duplicate the Nodes with an existing
        EdgePricer before creating a Graph from the lists anyway. */
        nodes.add(new Node(player, pos, nodes.size(), edges, null, partialEdges));
    }

    /**
     * Creates new Lists, Nodes and Edges so that separate Graph instances will not
     * reference the same compositing objects.
     */
    private void duplicate(EdgePricer pricer) {
        List<Node> newNodes = new ArrayList<Node>();
        List<Edge> newEdges = new LinkedList<Edge>();
        partialEdges = new LinkedList<PartialEdge>();
        for (Node n : nodes) {
            newNodes.add(new Node(n, newEdges, pricer, partialEdges));
        }
        for (Edge e : edges) {
            newEdges.add(new Edge(newNodes.get(e.node1.id), newNodes.get(e.node2.id)));
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
