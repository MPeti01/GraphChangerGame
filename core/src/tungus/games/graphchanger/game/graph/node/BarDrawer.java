package tungus.games.graphchanger.game.graph.node;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.DrawUtils;

class BarDrawer {

    private final Vector2 leftEnd;
    private final Vector2 rightEnd;
    private final float length, width;
    private final Color leftColor;
    private final Color rightColor;

    public BarDrawer(Vector2 middleLeft, float length, float width) {
        this.leftEnd = middleLeft;
        this.rightEnd = new Vector2(leftEnd.x + length, leftEnd.y);
        this.width = width;
        this.length = length;
        leftColor = new Color(Color.GREEN);
        rightColor = new Color(Color.CLEAR);
    }

    public void setColor(Color left, Color right) {
        this.leftColor.set(left);
        this.rightColor.set(right);
    }

    private Vector2 middle = new Vector2();

    public void draw(SpriteBatch batch, float divide) {
        Color original = batch.getColor();
        middle.set(leftEnd).add(length*divide, 0);

        batch.setColor(leftColor);
        DrawUtils.drawLine(batch, leftEnd, middle, width);

        batch.setColor(rightColor);
        DrawUtils.drawLine(batch, middle, rightEnd, width);

        batch.setColor(original);
    }
}
