package tungus.games.graphchanger.game.players;

import com.badlogic.gdx.graphics.Color;

public enum Player {
    P1(Color.BLUE, new Color(0.7f, 0.85f, 1f, 1f), new Color(0.1f, 0.2f, 0.5f, 1f)),
    P2(Color.RED, new Color(1f, 0.5f, 0.5f, 1f), new Color(0.5f, 0.1f, 0.1f, 1f));

    public final Color mainColor;
    public final Color edgeColor;
    public final Color backColor;

    Player(Color m, Color e, Color b) {
        mainColor = m;
        edgeColor = e;
        backColor = b;
    }
}
