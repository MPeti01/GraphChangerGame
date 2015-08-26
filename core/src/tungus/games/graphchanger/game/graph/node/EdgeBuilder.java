package tungus.games.graphchanger.game.graph.node;

import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.game.graph.EdgePricer;
import tungus.games.graphchanger.game.graph.PartialEdge;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the creation of Edges by consuming passing Units.
 */
class EdgeBuilder implements PartialEdge.EdgeCompleteListener {

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
        PartialEdge newEdge = new PartialEdge(thisNode, goal, price, 0, this);
        edgesToBuild.add(newEdge);
        allPartialEdges.add(newEdge);

        pricer.edgeBuilt(thisNode.player());
    }

    public PartialEdge edgeToBuild() {
        return edgesToBuild.isEmpty() ? null : edgesToBuild.get(0);
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
                PartialEdge newEdge = new PartialEdge(start, end, edgeThere.totalCost, edgeThere.progress(), this);
                edgesToBuild.add(newEdge);
                allPartialEdges.add(newEdge);
            } else {
                edgesToBuild.get(i).set(other.edgesToBuild.get(i));
            }
        }
    }

    public void stopEdgeTo(Node other) {
        Iterator<PartialEdge> it = edgesToBuild.iterator();
        while (it.hasNext()) {
            PartialEdge e = it.next();
            if (e.endNode() == other) {
                allPartialEdges.remove(e);
                it.remove();
                break;
            }
        }
    }

    @Override
    public void onEdgeComplete(PartialEdge built) {
        if (built != edgesToBuild.get(0))
            throw new GdxRuntimeException("Completed non-front edge, makes no sense!");

        allPartialEdges.remove(built);
        edgesToBuild.remove(0);
        thisNode.addEdgeTo(built.endNode());
    }

    public void reachingWithEdge(Node source, float progress) {
        Iterator<PartialEdge> it = edgesToBuild.iterator();
        while (it.hasNext()) {
            PartialEdge edge = it.next();
            if (edge.endNode() == source) {
                if (progress == 1) {
                    allPartialEdges.remove(edge);
                    it.remove();
                }
                else {
                    edge.boundProgress(1 - progress);
                }
            }
        }
    }
}
