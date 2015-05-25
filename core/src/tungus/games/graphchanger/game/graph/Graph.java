package tungus.games.graphchanger.game.graph;

import tungus.games.graphchanger.game.graph.editor.Move;
import tungus.games.graphchanger.game.players.Army;

import java.util.Iterator;
import java.util.List;

/**
 * Class representing the game's "map" or graph. Handles/delegates loading from file, updating.
 * Can copy data from another instance.
 */
public class Graph {

    public final List<Node> nodes;
    public final List<Edge> edges;
    private final DirectionCalculator directionCalculator;

    public Graph(List<Node> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
        directionCalculator = new DirectionCalculator(this.nodes.size());
    }

    public void updateNodes(float delta) {
        directionCalculator.setDirections(nodes);
        for (Node n : nodes) {
            n.update(delta);
        }
    }

    public void set(Graph other, Army p1, Army p2) {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).set(other.nodes.get(i), p1, p2);
        }
        setEdges(other.edges);
    }

    private void setEdges(List<Edge> other) {
        Iterator<Edge> it = edges.iterator();
        while (it.hasNext()) {
            Edge here = it.next();
            if (!other.contains(here)) {
                it.remove();
            }
        }
        for (Edge there : other) {
            if (!edges.contains(there)) {
                edges.add(new Edge(nodes.get(there.node1.id), nodes.get(there.node2.id)));
            }
        }
    }

    public void applyMove(Move m) {
        Node n1 = nodes.get(m.node1ID);
        Node n2 = nodes.get(m.node2ID);
        if (m.add) {
            n1.addNeighbor(n2);
            n2.addNeighbor(n1);
            edges.add(new Edge(n1, n2));
        } else {
            n1.removeNeighbor(n2);
            n2.removeNeighbor(n1);
            edges.remove(new Edge(n1, n2));
        }
    }

}