package tungus.games.graphchanger.game.graph.node;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.players.Player;

public class CaptureHandler {

    private static final float BAR_LENGTH = 50;
    private static final float BAR_WIDTH = 7;
    private static final float BAR_DIST_ABOVE = 25;

    private Player owner;
    private Player attacker = null;
    private int captureProgress = 0;

    public Upgrader upgrader; // TODO Fix this horrible implementation
    private final BarDrawer bar;

    public CaptureHandler(Player initialOwner, Vector2 pos) {
        owner = initialOwner;
        bar = new BarDrawer(pos.cpy().add(-BAR_LENGTH/2, BAR_DIST_ABOVE), BAR_LENGTH, BAR_WIDTH);
    }

    public boolean usesUnitPassingFrom(Player passing) {
        if (isUnderAttack()) {
            if (passing == attacker)
                captureProgress++;
            else
                captureProgress--;
        } else if (passing != owner) {
            captureProgress++;
            attacker = passing;
        } else {
            return false;
        }
        if (captureProgress == upgrader.unitsToCapture()) {
            owner = attacker;
            captureProgress = 0;
        }
        return true;
    }

    public boolean wouldUseUnitFrom(Player p) {
        return p != owner || isUnderAttack();
    }

    public Player owner() {
        return owner;
    }

    public boolean isUnderAttack() {
        return captureProgress > 0;
    }

    public void set(CaptureHandler other) {
        owner = other.owner;
        attacker = other.attacker;
        captureProgress = other.captureProgress;
    }

    public void renderBar(SpriteBatch batch) {
        bar.setColor(attacker == null ? Color.WHITE : attacker.color(),
                        owner == null ? Color.WHITE : owner.color());
        bar.draw(batch, (float)captureProgress/upgrader.unitsToCapture());
    }
}
