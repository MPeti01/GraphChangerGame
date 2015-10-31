package tungus.games.graphchanger.game.graph.node;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.players.Player;

class Upgrader {

    public static final float[] SPAWN_TIMES = new float[]{3.6f, 1.2f, 0.4f};
    public static final int[] UPGRADE_COSTS = new int[]{0, 16, 40};
    public static final int[] UNITS_TO_CAP_NEUTRAL = new int[]{3, 8, 16};
    public static final int[] UNITS_TO_CAP_PLAYER = new int[]{8, 20, 50};

    public static final float EFFECT_SIZE_MULTIPLIER = 1.6f;

    private static final float BAR_LENGTH = 50;
    private static final float BAR_WIDTH = 7;
    private static final float BAR_DIST_BELOW = 25;

    public int level;
    private int unitsNeeded = 0;
    private Player owner;

    private final BarDrawer bar;
    private boolean justUpgraded = false;

    public Upgrader(Player o, Vector2 pos, int level) {
        owner = o;
        bar = new BarDrawer(pos.cpy().add(-BAR_LENGTH/2, -BAR_DIST_BELOW), BAR_LENGTH, BAR_WIDTH);
        bar.setColor(Color.GREEN, Color.WHITE);
        this.level = level;
    }

    public void setOwner(Player p) {
        owner = p;
    }

    public void startUpgrade() {
        if (level < UPGRADE_COSTS.length-1 && !upgrading())
            unitsNeeded = UPGRADE_COSTS[level+1];
    }

    public boolean usesUnitPassingFrom(Player p) {
        justUpgraded = false;
        if (wouldUseUnitFrom(p)) {
            unitsNeeded--;
            if (unitsNeeded == 0) {
                level++;
                justUpgraded = true;
            }
            return true;
        }
        return false;
    }

    public boolean wouldUseUnitFrom(Player p) {
        return unitsNeeded > 0 && p == owner;
    }

    public float spawnReload() {
        return SPAWN_TIMES[level];
    }

    public boolean upgrading() {
        return unitsNeeded > 0;
    }

    public void render(SpriteBatch batch, float delta) {
        if (unitsNeeded > 0) {
            bar.draw(batch, 1 - (float)unitsNeeded / UPGRADE_COSTS[level+1]);
        }
    }

    public int unitsToCapture() {
        return owner == null ? UNITS_TO_CAP_NEUTRAL[level] : UNITS_TO_CAP_PLAYER[level];
    }

    public void set(Upgrader other) {
        level = other.level;
        owner = other.owner;
        unitsNeeded = other.unitsNeeded;
    }

    public boolean justUpgraded() {
        if (justUpgraded) {
            justUpgraded = false;
            return true;
        } else
            return false;
    }
}
