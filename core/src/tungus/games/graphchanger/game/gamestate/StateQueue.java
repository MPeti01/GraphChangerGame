package tungus.games.graphchanger.game.gamestate;

import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.graph.GraphLoader;
import tungus.games.graphchanger.game.players.Army;
import tungus.games.graphchanger.game.players.Player;

/**
 * Stores the GameStates for resimulating the last frames if needed.<br>
 * Not a queue in the usual sense: Stores a fixed number of States, ordered from top to bottom.
 * Can rotate the bottom one to the top and can return the State object a given distance below the top one.
 */
class StateQueue {
    /**
     * The states of the past STORED_TICKS ticks. Array used for arbitrary element access.
     */
    private final GameState[] queue;
    private final int size;
    private int queueHead = 0;

    public StateQueue(GraphLoader loader, int size) {
        this.size = size;
        queue = new GameState[this.size];
        loader.load();
        for (int i = 0; i < this.size; i++)
        {
            loader.duplicate(); // Create new objects to avoid Node/Edge/List instance sharing between States
            Graph g = new Graph(loader.nodes, loader.edges);
            Army a1 = new Army(Player.P1);
            Army a2 = new Army(Player.P2);
            queue[i] = new GameState(g, a1, a2);
        }
    }

    /**
     * Remove the GameState from the bottom of the queue and add it to the top, making it the new head.
     */
    public void rotate() {
        queueHead = (queueHead+1) % size;
    }

    /**
     * Returns the GameState a given distance below the top (head) of the queue.
     */
    public GameState atDepth(int d) {
        return queue[(queueHead - d + size) % size];
    }
}
