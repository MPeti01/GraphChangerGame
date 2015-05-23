package tungus.games.graphchanger.game.gamestate;

import com.badlogic.gdx.utils.IntMap;
import tungus.games.graphchanger.game.graph.Graph;
import tungus.games.graphchanger.game.graph.GraphEditor;
import tungus.games.graphchanger.game.players.Army;
import tungus.games.graphchanger.game.players.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a queue of GameStates, updates the game to the head of the queue and handles incoming moves,
 * resimulating a few ticks if a move happened some ticks earlier than the current one.
 */
public class GameSimulator implements MoveListener {
    private static final float TICK_TIME = 0.1f; // 10 ticks per sec
    private static final int STORED_TICKS = 15;

    /**
     * The states of the past STORED_TICKS ticks. Array used for arbitrary element access.
     */
    private final GameState[] queue = new GameState[STORED_TICKS];

    /**
     * Moves made after each frame
     */
    private final IntMap<List<Move>> movesEachTick = new IntMap<List<Move>>(STORED_TICKS *2);

    public GameSimulator(GraphEditor editor, String level) {
        for (int i = 0; i < STORED_TICKS; i++)
        {
            Army p1 = new Army(Player.P1);
            Army p2 = new Army(Player.P2);
            Graph g = new Graph(editor, level, p1, p2);
            queue[i] = new GameState(g, p1, p2);
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
            GameState old = queue[(queueHead - (currentTickNum-tickNum)) % STORED_TICKS];
            GameState next = queue[(queueHead - (currentTickNum-tickNum) + 1) % STORED_TICKS];

            next.set(old);
            List<Move> movesToApply = movesEachTick.get(tickNum);
            if (movesToApply != null) {
                for (Move m : movesToApply) {
                    next.applyMove(m);
                }
                movesEachTick.remove(tickNum);
            }
            next.update(TICK_TIME);
        }
        currentTickNum++;
        queueHead = (queueHead+1) % STORED_TICKS;
        oldestNewMove = currentTickNum;
    }

    public void update(float delta) {
        timeSinceTick += delta;
        if (timeSinceTick >= TICK_TIME) {
            tick();
            timeSinceTick -= TICK_TIME;
        }
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
}
