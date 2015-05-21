package tungus.games.graphchanger.game.gamestate;

/**
 * Created by Peti on 2015.05.21..
 */
public interface MoveListener {
    public void addMove(Move m);
    public void addMove(Move m, int tickID);
}
