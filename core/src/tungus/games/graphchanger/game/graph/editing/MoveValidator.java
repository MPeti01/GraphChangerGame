package tungus.games.graphchanger.game.graph.editing;

import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.graph.node.Node;
import tungus.games.graphchanger.game.players.Player;

import java.util.List;

/**
 * Tests whether a player can make certain moves based on {@link Node} ownership and Edge/Node overlapping
 */
class MoveValidator {

    private final Player moveMaker;
    private final NodeFinder nodes;
    private final EdgeFinder edges;

    public MoveValidator(NodeFinder nodes, EdgeFinder edges, Player moveMaker) {
        this.moveMaker = moveMaker;
        this.nodes = nodes;
        this.edges = edges;
    }

    boolean canCut(List<Edge> edgesToCut) {
        if (edgesToCut.size() > 1) return false;
        if (edgesToCut.isEmpty()) return true;
        Edge edgeToCut = edgesToCut.get(0);
        return edgeToCut.node1.player() == moveMaker;
    }

    boolean canConnect(int node1ID, int node2ID) {
        Node node1 = nodes.withID(node1ID);
        Node node2 = nodes.withID(node2ID);
        return node1.player() == moveMaker
                && node1 != node2
                && nodes.nodesThrough(node1.pos(), node2.pos()).isEmpty()
                && edges.edgesThrough(node1.pos(), node2.pos()).isEmpty()
                && !edges.hasEdgeBetween(node1, node2);
    }

    boolean canUpgrade(Node node) {
        return node.player() == moveMaker;
    }

    boolean canStartEdgeFrom(Node node) {
        return node.player() == moveMaker;
    }
}
