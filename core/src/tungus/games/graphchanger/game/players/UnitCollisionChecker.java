package tungus.games.graphchanger.game.players;

/**
 * Checks for collisions between {@link Unit Units} of opposing {@link Army Armies} and removes them.
 */
public class UnitCollisionChecker {

    private static final float COLLISION_DIST = 10f;
    private static final float COLL_DIST2 = COLLISION_DIST*COLLISION_DIST;

    public void removeColliders(Army p1, Army p2) {
        // Backwards iteration for simple safe removal
        for (int i = p1.units.size()-1; i >= 0; i--) {
            Unit u1 = p1.units.get(i);
            boolean killed = false;
            for (int j = p2.units.size()-1; j >= 0; j--) {
                Unit u2 = p2.units.get(j);
                if (u1.pos.dst2(u2.pos) < COLL_DIST2) {
                    u1.kill();
                    u2.kill();
                    killed = true;
                    break;
                }
            }
            if (killed) break;
        }
    }
}
