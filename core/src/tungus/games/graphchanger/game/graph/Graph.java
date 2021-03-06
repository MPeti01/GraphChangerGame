package tungus.games.graphchanger.game.graph;

import tungus.games.graphchanger.game.graph.node.DirectionCalculator;
import tungus.games.graphchanger.game.graph.node.Node;
import tungus.games.graphchanger.game.players.Army;

import java.util.List;

/**
 * Class representing the game's "map" or graph. Handles/delegates loading from file, updating.
 * Can copy data from another instance.
 */
public class Graph {

    public final List<Node> nodes;
    public final List<Edge> edges;
    public final List<PartialEdge> partialEdges;
    private final DirectionCalculator directionCalculator;

    public Graph(List<Node> nodes, List<Edge> edges, List<PartialEdge> partialEdges) {
        this.nodes = nodes;
        this.edges = edges;
        this.partialEdges = partialEdges;
        directionCalculator = new DirectionCalculator(this.nodes.size());
    }

    public void updateNodes(float delta, Army... armies) {
        directionCalculator.setDirections(nodes);
        for (Node n : nodes) {
            n.update(delta, armies);
        }
    }

    public void addEdge(int n1, int n2) {
        nodes.get(n1).buildEdgeTo(nodes.get(n2));
    }

    public void removeEdge(int n1, int n2) {
        nodes.get(n1).removeEdgeTo(nodes.get(n2));
    }

    public void set(Graph other) {
        // Edges are set by Nodes when they find missing / additional neighbors
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).set(other.nodes.get(i));
        }
    }

}