package tungus.games.graphchanger.game.graph;

import tungus.games.graphchanger.game.players.Player;

/**
 * Tests whether a player can make certain moves based on Node ownership
 */
class MoveValidator {

    private final Player moveMaker;

    public MoveValidator(Player moveMaker) {
        this.moveMaker = moveMaker;
    }

    boolean canCut(Edge edgeToCut) {
        return edgeToCut.node1.player() == moveMaker && edgeToCut.node2.player() == moveMaker;
    }

    boolean canConnect(Node node1, Node node2) {
        return node1.player() == moveMaker || node2.player() == moveMaker;
    }

    boolean canStartEdgeFrom(Node node) {
        return node.player() == moveMaker;
    }
}
