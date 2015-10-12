package tungus.games.graphchanger.game.graph.node;

import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.graph.EdgePricer;
import tungus.games.graphchanger.game.graph.PartialEdge;

import java.util.Collections;
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

    private int nextDestinationIndex = 0;

    public EdgeBuilder(Node thisNode, EdgePricer pricer, List<Node> allNodes, List<PartialEdge> partialEdges) {
        this.pricer = pricer;
        this.thisNode = thisNode;
        this.allNodes = allNodes;
        allPartialEdges = partialEdges;
    }

    public void startEdgeTo(Node goal) {
        createPartialEdge(goal, 0);
        pricer.edgeBuilt(thisNode.player());
    }

    private PartialEdge createPartialEdge(Node goal, float progress) {
        int price = pricer.totalPrice(thisNode, goal);
        PartialEdge newEdge = new PartialEdge(thisNode, goal, price, progress, this);
        edgesToBuild.add(newEdge);
        allPartialEdges.add(newEdge);
        return newEdge;
    }

    public PartialEdge nextToBuild() {
        if (edgesToBuild.size() == 0) return null;
        int s = edgesToBuild.size();
        PartialEdge toBuild = edgesToBuild.get(nextDestinationIndex % s);
        nextDestinationIndex = (nextDestinationIndex + 1) % s;
        return toBuild;
    }

    public boolean isBuilding() {
        return !edgesToBuild.isEmpty();
    }

    public void stopEdgeTo(Node other) {
        Iterator<PartialEdge> it = edgesToBuild.iterator();
        while (it.hasNext()) {
            PartialEdge e = it.next();
            if (e.endNode() == other) {
                e.cut();
                allPartialEdges.remove(e);
                it.remove();
                break;
            }
        }
    }

    @Override
    public void onEdgeComplete(PartialEdge built) {
        allPartialEdges.remove(built);
        edgesToBuild.remove(built);
        Edge finished = thisNode.addEdgeTo(built.endNode());
        finished.setEffect(built.getEffect());
        built.finishAs(finished);
    }

    public PartialEdge reachingWithEdge(Node source, float progress, boolean removedFullEdge) {
        if (removedFullEdge) {
            return createPartialEdge(source, 1 - progress);
        } else {
            for (PartialEdge edge : edgesToBuild) {
                if (edge.endNode() == source) {
                    edge.boundProgress(1 - progress);
                    return edge;
                }
            }
            return null;
        }
    }

    public void clearEdges() {
        while (!edgesToBuild.isEmpty()) {
            PartialEdge e = edgesToBuild.get(0);
            e.cut();
            allPartialEdges.remove(e);
            edgesToBuild.remove(0);
        }
    }

    public boolean isContesting(Node neighbor) {
        for (PartialEdge e : edgesToBuild)
            if (e.endNode() == neighbor) return true;
        return false;
    }

    public PartialEdge nextContested() {
        int s = edgesToBuild.size();
        if (s == 0) return null;
        int i = (nextDestinationIndex %= s);
        //int i = (lastDestinationIndex +1)%s; i != lastDestinationIndex; i = (i+1)%s) {
        do {
            PartialEdge toBuild = edgesToBuild.get(i);
            if (toBuild.endNode().isContesting(thisNode)) {
                nextDestinationIndex = (i + 1) % s;
                return toBuild;
            }
            i = (i + 1) % s;
        } while (i != nextDestinationIndex);
        return null;
    }

    public void set(EdgeBuilder other) {
        nextDestinationIndex = other.nextDestinationIndex;
        for (Iterator<PartialEdge> it = edgesToBuild.iterator(); it.hasNext(); ) {
            PartialEdge edgeHere = it.next();
            boolean found = false;
            for (PartialEdge edgeThere : other.edgesToBuild) {
                if (edgeHere.equals(edgeThere)) {
                    edgeHere.set(edgeThere);
                    found = true;
                    break;
                }
            }
            if (!found) {
                it.remove();
                allPartialEdges.remove(edgeHere);
            }
        }
        if (edgesToBuild.size() == other.edgesToBuild.size()) return;
        for (PartialEdge edgeThere : other.edgesToBuild) {
            if (!edgesToBuild.contains(edgeThere)) {
                Node start = allNodes.get(edgeThere.startNode().id);
                Node end = allNodes.get(edgeThere.endNode().id);
                PartialEdge newEdge = new PartialEdge(start, end, edgeThere.totalCost, edgeThere.progress(), this);
                edgesToBuild.add(newEdge);
                allPartialEdges.add(newEdge);
            }
        }
        Collections.sort(edgesToBuild); // Ensure consistent order
    }
}
