package tungus.games.graphchanger.game.graph;

/**
 * Decides whether the {@link Node} it's attached to can spawn a new unit.
 */
class UnitSpawnController {

    private static final float SPAWN_RELOAD = 2f;
    //private static final int MAX_PASSES_PER_SEC = 3;

    // Could be optimized by implementing a deque of floats
    //private List<Float> passTimes = new LinkedList<Float>();

    private float time = 0;
    private float lastSpawn = -SPAWN_RELOAD;

    public void unitPassedBy() {
        //passTimes.add(time);
    }

    public void update(float delta) {
        time += delta;
        /*for (Iterator<Float> it = passTimes.iterator(); it.hasNext();) {
            Float pass = it.next();
            if (pass < time - 1f) {
                it.remove();
            } else {
                break;
            }
        }*/
    }

    public boolean shouldSpawn() {
        if (/*passTimes.size() < MAX_PASSES_PER_SEC && */time > lastSpawn + SPAWN_RELOAD) {
            lastSpawn = time;
            return true;
        } else {
            return false;
        }
    }

    public void set(UnitSpawnController other) {
        time = other.time;
        lastSpawn = other.lastSpawn;
    }
}
