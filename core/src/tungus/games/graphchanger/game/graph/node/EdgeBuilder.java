package tungus.games.graphchanger.game.graph.node;

import tungus.games.graphchanger.game.graph.Edge;

import java.util.LinkedList;
import java.util.List;

/**
 * Handles the creation of Edges by consuming passing Units.
 */
public class EdgeBuilder {

    private static int edgesBuilt = 0;
    public static int edgeCostMultiplier() {
        return edgesBuilt/4 + 1;
    }

    private List<Edge> edgesToBuild = new LinkedList<Edge>();

    public void startEdge(Edge e) {
        edgesToBuild.add(e);
        edgesBuilt++;
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
