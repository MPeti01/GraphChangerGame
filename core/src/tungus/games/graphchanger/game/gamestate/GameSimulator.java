package tungus.games.graphchanger.game.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import tungus.games.graphchanger.game.graph.EdgePricer;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.graph.editing.moves.Move;
import tungus.games.graphchanger.game.graph.editing.moves.MoveListener;
import tungus.games.graphchanger.game.graph.load.GraphLoader;
import tungus.games.graphchanger.game.players.Army;
import tungus.games.graphchanger.game.players.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles updating the gameplay. <br>
 * Stores a queue of {@link GameState GameStates}, updates the game to the head of the queue and handles incoming moves,
 * resimulating a few ticks if a move happened some ticks earlier than the current one.
 */
public class GameSimulator implements MoveListener {
    public static final float TICK_TIME = 0.1f; // 20 ticks per sec

    private final GameState gameState;

    /**
     * Moves made after each frame. Only ever contains currentTickNum and currentTickNum+1 keys.
     */
    private final IntMap<List<Move>> movesEachTick = new IntMap<List<Move>>(2);

    private int currentTickNum = 0;
    private float timeSinceTick = 0;

    public GameSimulator(GraphLoader loader) {
        loader.load();
        EdgePricer pricer = new EdgePricer();
        Graph g = loader.createGraph(pricer);
        Army a1 = new Army(Player.P1);
        Army a2 = new Army(Player.P2);
        gameState = new GameState(g, pricer, a1, a2);
    }

    public void addMove(Move m) {
        addMove(m, currentTickNum);
    }

    public void addMove(Move m, int tickNum) {
        if (tickNum < currentTickNum) {
            throw new IllegalArgumentException("Already dropped tick " + tickNum + ", can't add move");
        }
        List<Move> list = movesEachTick.get(tickNum);
        if (list == null) {
            list = new ArrayList<Move>();
            movesEachTick.put(tickNum, list);
        }
        list.add(m);
    }

    private void tick() {
        List<Move> movesToApply = movesEachTick.get(currentTickNum);
        if (movesToApply != null) {
            for (Move m : movesToApply) {
                gameState.applyMove(m);
                Gdx.app.log("MOVES", "Applied move [" + m.toString() + "] to state " + currentTickNum);
            }
        }
        gameState.update(TICK_TIME);
        movesEachTick.remove(currentTickNum);
        currentTickNum++;
    }

    public void timePassed(float delta) {
        timeSinceTick += delta;
    }

    /**
     * Simulates tick(s) if the last one was more than TICK_TIME ago.
     * @return <code>true</code> if a tick was simulated
     */
    public boolean update() {
        if (timeSinceTick >= TICK_TIME) {
            tick();
            timeSinceTick -= TICK_TIME;
            return true;
        }
        return false;
    }

    public float timeSinceTick() {
        return timeSinceTick;
    }

    public GameState state() {
        return gameState;
    }
}
