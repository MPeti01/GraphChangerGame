package tungus.games.graphchanger.game.graph.node;

import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.graph.EdgePricer;

import java.util.LinkedList;
import java.util.List;

/**
 * Handles the creation of Edges by consuming passing Units.
 */
public class EdgeBuilder {

    private final EdgePricer pricer;

    public EdgeBuilder(EdgePricer pricer) {
        this.pricer = pricer;
    }

    private List<Edge> edgesToBuild = new LinkedList<Edge>();

    public void startEdge(Edge e) {
        edgesToBuild.add(e);
        pricer.edgeBuilt(e.node1.player());
    }

    public Node unitUsed() {
        Edge toBuild = edgesToBuild.get(0);
        toBuild.unitArrived();
        if (toBuild.isComplete()) {
            edgesToBuild.remove(0);
            return toBuild.node2;
        } else {
            return null;
        }
    }

    public boolean isBuilding() {
        return !edgesToBuild.isEmpty();
    }

    public void set(EdgeBuilder other, List<Edge> allEdges) {
        edgesToBuild.clear();
        for (Edge there : other.edgesToBuild) {
            for (Edge here : allEdges) {
                if (here.equals(there)) {
                    edgesToBuild.add(here);
                    break;
                }
            }
        }
    }
}
