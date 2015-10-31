package tungus.games.graphchanger.game.players;

import com.badlogic.gdx.graphics.Color;

public enum Player {
    P1(new Color(0.15f, 0.58f, 1.00f, 1f), new Color(0.11f, 0.34f, 0.62f, 1f), new Color(0.1f, 0.2f, 0.5f, 0.3f)),
    P2(new Color(1.00f, 0.38f, 0.15f, 1f), new Color(0.62f, 0.25f, 0.11f, 1f), new Color(0.5f, 0.2f, 0.1f, 0.3f));

    public final Color mainColor;
    public final Color edgeColor;
    public final Color backColor;

    Player(Color m, Color e, Color b) {
        mainColor = m;
        edgeColor = e;
        backColor = b;
    }
}
