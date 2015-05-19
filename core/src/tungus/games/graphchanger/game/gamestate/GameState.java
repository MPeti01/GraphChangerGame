package tungus.games.graphchanger.game.gamestate;

import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.players.Army;
import tungus.games.graphchanger.game.players.Player;

/**
 * Created by Peti on 2015.05.14..
 */
public class GameState {
    public final Graph graph;
    public final Army p1 = new Army(Player.P1);
    public final Army p2 = new Army(Player.P2);

    public GameState(Graph graph) {
        this.graph = graph;
    }

    public void set(GameState other) {
        graph.set(other.graph);
        p1.set(other.p1, graph);
        p2.set(other.p2, graph);
    }

    public void set(Graph graph, Army p1, Army p2) {
        this.graph.set(graph);
        this.p1.set(p1, graph);
        this.p2.set(p2, graph);
    }

}
