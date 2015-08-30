package tungus.games.graphchanger.game.graph.load;

import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.graph.EdgePricer;
import tungus.games.graphchanger.game.graph.PartialEdge;
import tungus.games.graphchanger.game.graph.node.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class GraphLoader {

    public List<Node> nodes = new ArrayList<Node>();
    public List<Edge> edges = new LinkedList<Edge>();
    public List<PartialEdge> partialEdges = new LinkedList<PartialEdge>();

    public abstract void load(EdgePricer pricer);

    public void duplicate(EdgePricer pricer) {
        List<Node> newNodes = new ArrayList<Node>();
        List<Edge> newEdges = new LinkedList<Edge>();
        partialEdges = new LinkedList<PartialEdge>();
        for (Node n : nodes) {
            newNodes.add(new Node(n, newNodes, newEdges, pricer, partialEdges));
        }
        for (Edge e : edges) {
            newEdges.add(new Edge(newNodes.get(e.node1.id), newNodes.get(e.node2.id)));
        }
        nodes = newNodes;
        edges = newEdges;
    }
}
