package tungus.games.graphchanger.game.players;

import com.badlogic.gdx.graphics.Color;

public enum Player {
    P1(Color.BLUE), P2(Color.RED);

    private final Color color;

    Player(Color c) {
        color = c;
    }

    public Color color() {
        return color;
    }
}
