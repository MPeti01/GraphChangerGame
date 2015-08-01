package tungus.games.graphchanger.game.graph.editing.moves;

/**
 * Delegates to a list of other MoveListeners. 
 */
public class MoveListenerMultiplexer implements MoveListener {

    private final MoveListener[] listeners;

    public MoveListenerMultiplexer(MoveListener... listeners) {
        this.listeners = listeners;
    }

    @Override
    public void addMove(Move m) {
        for (MoveListener l : listeners) {
            l.addMove(m);
        }
    }

    @Override
    public void addMove(Move m, int tickID) {
        for (MoveListener l : listeners) {
            l.addMove(m, tickID);
        }
    }
}
