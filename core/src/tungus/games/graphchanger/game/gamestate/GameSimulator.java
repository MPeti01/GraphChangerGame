package tungus.games.graphchanger.game.gamestate;

import com.badlogic.gdx.utils.IntMap;
import tungus.games.graphchanger.game.graph.editing.moves.Move;
import tungus.games.graphchanger.game.graph.editing.moves.MoveListener;
import tungus.games.graphchanger.game.graph.load.GraphLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles updating the gameplay. <br>
 * Stores a queue of {@link GameState GameStates}, updates the game to the head of the queue and handles incoming moves,
 * resimulating a few ticks if a move happened some ticks earlier than the current one.
 */
public class GameSimulator implements MoveListener {
    private static final float TICK_TIME = 0.05f; // 20 ticks per sec
    private static final float DELAY_TOLERANCE = 3f; // TODO Eventually move to some connection oriented class
    private static final int STORED_TICKS = (int)Math.ceil(DELAY_TOLERANCE / TICK_TIME);

    private final StateQueue queue;

    /**
     * Moves made after each frame
     */
    private final IntMap<List<Move>> movesEachTick = new IntMap<List<Move>>(STORED_TICKS *2);

    private int currentTickNum = 0;
    private int oldestNewMove = 0;
    private float timeSinceTick = 0;

    public GameSimulator(GraphLoader loader) {
        queue = new StateQueue(loader, STORED_TICKS);
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

    private void tick() {
        // Iterate from oldest frame with a new move added to the latest frame
        for (int tickNum = oldestNewMove; tickNum <= currentTickNum; tickNum++)
        {
            // Get the frame in question, and the one following it
            GameState old = queue.atDepth(currentTickNum-tickNum);
            GameState next = queue.atDepth(currentTickNum-tickNum-1);

            next.set(old);
            List<Move> movesToApply = movesEachTick.get(tickNum);
            if (movesToApply != null) {
                for (Move m : movesToApply) {
                    next.applyMove(m);
                }
            }
            next.update(TICK_TIME);
        }
        queue.rotate();
        currentTickNum++;
        movesEachTick.remove(currentTickNum - STORED_TICKS);
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
        return queue.atDepth(0);
    }

}
