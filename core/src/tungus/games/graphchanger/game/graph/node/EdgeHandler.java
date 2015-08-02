package tungus.games.graphchanger.game.graph.node;

import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.players.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the edges at a Node. This includes building/removing them, listing them and giving directions to units.
 */
class EdgeHandler {
    private List<Edge> allEdgesInGraph;

    public final List<Node> inNeighbors = new LinkedList<Node>();
    public final List<Node> outNeighbors = new LinkedList<Node>();
    private final Node thisNode;
    private final EdgeBuilder builder = new EdgeBuilder();

    /**
     * Stores the neighboring node(s) with the closest neutral/enemy nodes that way. Empty if none reachable.
     */
    public final List<Node> primaryNeighbors = new ArrayList<Node>();
    private int nextDirectionIndex = 0;

    public EdgeHandler(Node node, List<Edge> edges) {
        allEdgesInGraph = edges;
        thisNode = node;
    }

    public Node destinationFromHere() {
        if (!primaryNeighbors.isEmpty())
            return nextDirection(primaryNeighbors);
        else
            return nextDirection(outNeighbors);
    }

    public void startEdgeTo(Node other) {
        Edge e = new Edge(thisNode, other);
        allEdgesInGraph.add(e);
        builder.startEdge(e);
    }

    public void addEdgeFrom(Node other) {
        inNeighbors.add(other);
    }

    public void removeEdgeTo(Node other) {
        outNeighbors.remove(other);
        other.removeEdgeFrom(thisNode);
        allEdgesInGraph.remove(new Edge(thisNode, other));
    }

    public void removeEdgeFrom(Node other) {
        inNeighbors.remove(other);
    }

    private Node nextDirection(List<Node> list) {
        if (list.size() == 0)
            return thisNode;
        nextDirectionIndex++;
        if (nextDirectionIndex >= list.size())
            nextDirectionIndex = 0;
        return list.get(nextDirectionIndex);
    }

    public boolean usesPassingUnitFrom(Player p) {
        if (!wouldUseUnitFrom(p)) return false;
        Node built = builder.unitUsed();
        if (built != null) {
            outNeighbors.add(built);
            built.addEdgeFrom(thisNode);
        }
        return true;
    }

    public boolean wouldUseUnitFrom(Player p) {
        return p == thisNode.player() && builder.isBuilding();
    }

    public void set(EdgeHandler other, List<Node> allNodes) {
        nextDirectionIndex = other.nextDirectionIndex;

        Iterator<Node> it = outNeighbors.iterator();
        while (it.hasNext()) {
            Node neighbor = it.next();
            if (!other.outNeighbors.contains(neighbor)) {
                it.remove();
            }
        }
        for (Node n : other.outNeighbors) {
            if (!outNeighbors.contains(n)) {
                outNeighbors.add(allNodes.get(n.id));
            }
        }

        it = inNeighbors.iterator();
        while (it.hasNext()) {
            Node neighbor = it.next();
            if (!other.inNeighbors.contains(neighbor)) {
                it.remove();
            }
        }
        for (Node n : other.inNeighbors) {
            if (!inNeighbors.contains(n)) {
                inNeighbors.add(allNodes.get(n.id));
            }
        }
        builder.set(other.builder, allEdgesInGraph);
    }

    public void clearOutNeighbors() {
        for (Node n : outNeighbors) {
            removeEdgeTo(n);
        }
    }
}
