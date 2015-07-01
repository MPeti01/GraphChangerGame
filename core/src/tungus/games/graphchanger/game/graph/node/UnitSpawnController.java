package tungus.games.graphchanger.game.graph.node;

/**
 * Decides whether the {@link Node} it's attached to can spawn a new unit.
 */
class UnitSpawnController {

    private final Upgrader reloadTeller;

    private float time = 0;
    private float lastSpawn = 0;

    public UnitSpawnController(Upgrader u) {
        reloadTeller = u;
    }

    public void update(float delta) {
        time += delta;
    }

    public boolean shouldSpawn() {
        if (time > lastSpawn + reloadTeller.spawnReload()) {
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
