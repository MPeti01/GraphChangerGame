package tungus.games.graphchanger.game.graph.node;

import tungus.games.graphchanger.game.players.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Calculates where Units should go from each given Node.
 * For each player, runs a breadth-first search from all nodes neighboring enemy or neutral nodes.
 */
public class DirectionCalculator {

    private final int[] distanceFromGoal;

    public DirectionCalculator(int nodeCount) {
        distanceFromGoal = new int[nodeCount];
    }

    public void setDirections(List<Node> nodes) {
        for (Node n : nodes) {
            n.primaryNeighbors.clear();
        }
        int s = nodes.size();
        for (int i = 0; i < s; i++) {
            distanceFromGoal[i] = -1;
        }
        setDirections(nodes, Player.P1);
        setDirections(nodes, Player.P2);
    }

    private void setDirections(List<Node> nodes, Player player) {
        Queue<Node> queue = new LinkedList<Node>();
        // Fill the queue with nodes bordering neutral/enemy nodes
        for (Node node : nodes) {
            if (node.player() == player) {
                boolean hasEnemyNeighbor = false;
                for (Node neighbor : node.neighbors) {
                    if (neighbor.player() != player) {
                        hasEnemyNeighbor = true;
                        node.primaryNeighbors.add(neighbor);
                        distanceFromGoal[node.id] = 1;
                    }
                }
                if (hasEnemyNeighbor) {
                    queue.add(node);
                }
            }
        }
        // Traverse nodes connected and set their directions
        while (!queue.isEmpty()) {
            Node node = queue.remove();
            for (Node neighbor : node.neighbors) {
                if (neighbor.player() == player) {
                    if (neighbor.primaryNeighbors.size() == 0) {
                        // Newly visited node, add to queue
                        neighbor.primaryNeighbors.add(node);
                        distanceFromGoal[neighbor.id] = distanceFromGoal[node.id] + 1;
                        queue.add(neighbor);
                    } else if (distanceFromGoal[neighbor.id] == distanceFromGoal[node.id] + 1) {
                        // Should send Units this way too
                        neighbor.primaryNeighbors.add(node);
                    }
                }
            }
        }
    }
}
