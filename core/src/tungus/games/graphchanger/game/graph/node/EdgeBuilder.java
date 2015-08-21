package tungus.games.graphchanger.game.graph.node;

import tungus.games.graphchanger.game.graph.EdgePricer;
import tungus.games.graphchanger.game.graph.PartialEdge;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the creation of Edges by consuming passing Units.
 */
public class EdgeBuilder {

    private final EdgePricer pricer;
    private final List<PartialEdge> allPartialEdges;
    private final List<Node> allNodes;

    private final Node thisNode;

    private List<PartialEdge> edgesToBuild = new LinkedList<PartialEdge>();

    public EdgeBuilder(Node thisNode, EdgePricer pricer, List<PartialEdge> partialEdges, List<Node> nodes) {
        this.pricer = pricer;
        this.thisNode = thisNode;
        allPartialEdges = partialEdges;
        allNodes = nodes;
    }

    public void startEdgeTo(Node goal) {
        int price = pricer.totalPrice(thisNode, goal);
        PartialEdge newEdge = new PartialEdge(thisNode, goal, price, 0);
        edgesToBuild.add(newEdge);
        allPartialEdges.add(newEdge);

        pricer.edgeBuilt(thisNode.player());
    }

    public Node unitUsed() {
        PartialEdge toBuild = edgesToBuild.get(0);
        toBuild.unitArrived();
        if (toBuild.isComplete()) {
            edgesToBuild.remove(0);
            allPartialEdges.remove(toBuild);
            return toBuild.endNode();
        } else {
            return null;
        }
    }

    public boolean isBuilding() {
        return !edgesToBuild.isEmpty();
    }

    public void set(EdgeBuilder other) {
        PartialEdge frontEdgeThere = other.edgesToBuild.isEmpty() ? null : other.edgesToBuild.get(0);
        for (Iterator<PartialEdge> it = edgesToBuild.iterator(); it.hasNext();) {
            PartialEdge edgeHere = it.next();
            if (!edgeHere.equals(frontEdgeThere)) {
                allPartialEdges.remove(edgeHere);
                it.remove();
            } else {
                break;
            }
        }
        for (int i = 0; i < other.edgesToBuild.size(); i++) {
            if (i > edgesToBuild.size()-1) {
                PartialEdge edgeThere = other.edgesToBuild.get(i);
                Node start = allNodes.get(edgeThere.startNode().id);
                Node end = allNodes.get(edgeThere.endNode().id);
                PartialEdge newEdge = new PartialEdge(start, end, edgeThere.totalCost, edgeThere.progress());
                edgesToBuild.add(newEdge);
                allPartialEdges.add(newEdge);
            } else {
                edgesToBuild.get(i).set(other.edgesToBuild.get(i));
            }
        }
    }
}
