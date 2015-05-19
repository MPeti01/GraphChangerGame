package tungus.games.graphchanger.game.gamestate;

import com.badlogic.gdx.utils.Pool;

/**
 * Created by Peti on 2015.05.14..
 */
class UnitInfo implements Pool.Poolable {

    static final Pool<UnitInfo> pool = new Pool<UnitInfo>(2000) {
        @Override
        protected UnitInfo newObject() {
            return new UnitInfo();
        }
    };

    float x, y;
    int goalID;

    private UnitInfo() {}


    static UnitInfo create(float x, float y, int goal) {
        UnitInfo info = pool.obtain();
        info.x = x;
        info.y = y;
        info.goalID = goal;
        return info;
    }

    @Override
    public void reset() {}
}
