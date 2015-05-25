package tungus.games.graphchanger.game.gamestate;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.IntMap;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.graph.GraphLoader;
import tungus.games.graphchanger.game.graph.editor.GraphEditor;
import tungus.games.graphchanger.game.graph.editor.Move;
import tungus.games.graphchanger.game.graph.editor.MoveListener;
import tungus.games.graphchanger.game.graph.editor.MoveListenerMultiplexer;
import tungus.games.graphchanger.game.players.Army;
import tungus.games.graphchanger.game.players.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles updating the gameplay. <br>
 * Stores a queue of {@link GameState GameStates}, updates the game to the head of the queue and handles incoming moves,
 * resimulating a few ticks if a move happened some ticks earlier than the current one.
 */
public class GameSimulator implements MoveListener {
    private static final float TICK_TIME = 0.1f; // 10 ticks per sec
    private static final int STORED_TICKS = 50;

    /**
     * The states of the past STORED_TICKS ticks. Array used for arbitrary element access.
     */
    private final GameState[] queue = new GameState[STORED_TICKS];

    /**
     * Moves made after each frame
     */
    private final IntMap<List<Move>> movesEachTick = new IntMap<List<Move>>(STORED_TICKS *2);

    public GameSimulator(FileHandle level, Player player, MoveListener... outsideListeners) {
        GraphLoader loader = new GraphLoader(level);
        for (int i = 0; i < STORED_TICKS; i++)
        {
            Army p1 = new Army(Player.P1);
            Army p2 = new Army(Player.P2);
            loader.load(p1, p2);
            Graph g = new Graph(loader.nodes, loader.edges);
            GraphEditor editor;
            if (outsideListeners.length == 0)
                editor = new GraphEditor(g, player, this);
            else {
                MoveListener[] listeners = Arrays.copyOf(outsideListeners, outsideListeners.length+1);
                listeners[listeners.length-1] = this;
                MoveListener multiplexer = new MoveListenerMultiplexer(listeners);
                editor = new GraphEditor(g, player, multiplexer);
            }
            queue[i] = new GameState(g, editor, p1, p2);
        }
    }
    public void addMove(Move m) {
        addMove(m, currentTickNum);
    }

    public void addMove(Move m, int tickNum) {
        if (tickNum <= currentTickNum - STORED_TICKS) {
            throw new IllegalArgumentException("Already dropped tick " + tickNum + ", can't add move");
        }
        List<Move> list = movesEachTick.get(tickNum);
        if (list == null) {
            list = new ArrayList<Move>();
            movesEachTick.put(tickNum, list);
        }
        list.add(m);
        oldestNewMove = Math.min(oldestNewMove, tickNum);
    }

    private int currentTickNum = 0;
    private int queueHead = 0;
    private int oldestNewMove = 0;
    private float timeSinceTick = 0;

    private void tick() {
        // Iterate from oldest frame with a new move added to the latest frame
        for (int tickNum = oldestNewMove; tickNum <= currentTickNum; tickNum++)
        {
            // Get the frame in question, and the one following it
            GameState old = queue[(queueHead - (currentTickNum-tickNum) + STORED_TICKS) % STORED_TICKS];
            GameState next = queue[(queueHead - (currentTickNum-tickNum) + STORED_TICKS + 1) % STORED_TICKS];

            next.set(old);
            List<Move> movesToApply = movesEachTick.get(tickNum);
            if (movesToApply != null) {
                for (Move m : movesToApply) {
                    next.applyMove(m);
                }
            }
            next.update(TICK_TIME);
        }
        currentTickNum++;
        movesEachTick.remove(currentTickNum - STORED_TICKS);
        queueHead = (queueHead+1) % STORED_TICKS;
        oldestNewMove = currentTickNum;
    }

    /**
     * Notes a given amount of time passed, and simulates a tick if the last one was more than TICK_TIME ago.
     * @param delta The time since the method was last called.
     * @return <code>true</code> if a tick was simulated
     */
    public boolean update(float delta) {
        timeSinceTick += delta;
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

    public GameState latestState() {
        return queue[queueHead];
    }

    public int latestTickNum() {
        return currentTickNum;
    }

    public int earliestStoredTick() {
        return currentTickNum - STORED_TICKS + 1;
    }
}
