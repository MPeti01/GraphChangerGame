package tungus.games.graphchanger.game.graph.editing.moves;

/**
 * Accepts a {@link Move}, possibly happened at a given earlier time.
 */
public interface MoveListener {
    public void addMove(Move m);
    public void addMove(Move m, int tickID);
}
