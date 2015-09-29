package tungus.games.graphchanger.game.players;

import com.badlogic.gdx.graphics.Color;

public enum Player {
    P1(new Color(0.04f, 0.52f, 0.89f, 1f), new Color(0.11f, 0.34f, 0.62f, 1f), new Color(0.1f, 0.2f, 0.5f, 1f)),
    P2(new Color(0.89f, 0.1f,  0.04f, 1f), new Color(0.62f, 0.07f, 0.11f, 1f), new Color(0.5f, 0.1f, 0.1f, 1f));

    public final Color mainColor;
    public final Color edgeColor;
    public final Color backColor;

    Player(Color m, Color e, Color b) {
        mainColor = m;
        edgeColor = e;
        backColor = b;
    }
}
