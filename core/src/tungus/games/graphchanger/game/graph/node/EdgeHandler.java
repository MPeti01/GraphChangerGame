package tungus.games.graphchanger.game.graph.node;

import tungus.games.graphchanger.game.graph.Destination;
import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.graph.EdgePricer;
import tungus.games.graphchanger.game.graph.PartialEdge;
import tungus.games.graphchanger.game.players.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the edges at a Node. This includes building/removing them, listing them and giving directions to units.
 */
class EdgeHandler {
    private List<Edge> allEdgesInGraph;

    public final List<Node> inNeighbors = new LinkedList<Node>();
    public final List<Node> outNeighbors = new LinkedList<Node>();
    public final List<Edge> outEdges = new LinkedList<Edge>();
    private final Node thisNode;
    private final EdgeBuilder builder;


    /**
     * Stores the neighboring node(s) with the closest neutral/enemy nodes that way. Empty if none reachable.
     */
    public final List<Edge> primaryNeighbors = new ArrayList<Edge>();
    private int nextDirectionIndex = 0;

    public EdgeHandler(Node node, List<Edge> edges, EdgePricer pricer, List<PartialEdge> partialEdges) {
        allEdgesInGraph = edges;
        thisNode = node;
        builder = new EdgeBuilder(node, pricer, partialEdges);
    }

    /**
     * Starts building an Edge towards the given Node.
     */
    public void startEdgeTo(Node other) {
        builder.startEdgeTo(other);
    }

    /**
     * Adds an incoming Edge to this Node.
     * Only to be called when the Edge is added to the other Node and the Edge list in other.addEdgeTo(node)
     */
    public void addEdgeFrom(Node other) {
        inNeighbors.add(other);
    }

    /**
     * Instantly creates an Edge between thisNode and the given other Node.
     * Includes adding the Edge to the list an notifying the other Node to add an inNeighbor.
     */
    public Edge addEdgeTo(Node other) {
        outNeighbors.add(other);
        Edge e = new Edge(thisNode, other);
        outEdges.add(e);
        allEdgesInGraph.add(e);
        other.addEdgeFrom(thisNode);
        return e;
    }

    public void removeEdgeTo(Node other) {
        outNeighbors.remove(other);
        other.removeEdgeFrom(thisNode);
        removeFromEdgeLists(other);
        builder.stopEdgeTo(other);
    }

    /**
     * Handles the removal of an Edge instance to a given Node and returns it
     */
    private Edge removeFromEdgeLists(Node goal) {
        Edge toRemove = null;
        for (Edge e : outEdges) {
            if (e.node2.equals(goal)) {
                toRemove = e;
                break;
            }
        }
        if (toRemove != null) {
            toRemove.cut();
            outEdges.remove(toRemove);
            allEdgesInGraph.remove(toRemove);
            return toRemove;
        } else {
            return null;
        }
    }



    public void removeEdgeFrom(Node other) {
        inNeighbors.remove(other);
    }

    public Destination destinationFromHere() {
        PartialEdge toBuild = builder.nextToBuild();
        if (toBuild != null) {
            return toBuild;
        } else {
            if (!primaryNeighbors.isEmpty())
                return nextDirection(primaryNeighbors);
            else
                return nextDirection(outEdges);
        }
    }

    private Destination nextDirection(List<? extends Destination> list) {
        if (list.size() == 0)
            return thisNode;
        nextDirectionIndex++;
        if (nextDirectionIndex >= list.size())
            nextDirectionIndex = 0;
        return list.get(nextDirectionIndex);
    }

    public boolean wouldUseUnitFrom(Player p) {
        return p == thisNode.player() && builder.isBuilding();
    }

    public void clearOutNeighbors() {
        while(!outNeighbors.isEmpty()) {
            removeEdgeTo(outNeighbors.get(0));
        }
        primaryNeighbors.clear();
        builder.clearEdges();
    }

    public void reachingWithEdge(Node source, float progress) {
        if (outNeighbors.contains(source)) {
            outNeighbors.remove(source);
            source.removeEdgeFrom(thisNode);
            PartialEdge replacer = builder.reachingWithEdge(source, progress, true);
            Edge removed = removeFromEdgeLists(source);
            removed.contestedAs(replacer);

        } else {
            builder.reachingWithEdge(source, progress, false);
        }

    }

    public void addPrimaryNeighbor(Node node) {
        for (Edge e : outEdges) {
            if (e.node2.equals(node)) {
                primaryNeighbors.add(e);
                break;
            }
        }
    }

    public boolean isContesting(Node neighbor) {
        return outNeighbors.contains(neighbor) || builder.isContesting(neighbor);
    }

    public PartialEdge contestedEdge() {
        return builder.nextContested();
    }
}
