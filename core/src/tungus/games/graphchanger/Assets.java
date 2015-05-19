package tungus.games.graphchanger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Peti on 2015.03.19..
 */
public class Assets {
    public static enum Tex {
        NODE0, NODE1, NODE2, NODE_SELECTED, LINE, UNIT1;

        private final String filename;
        public TextureRegion t = null;

        Tex(String path) {
            this.filename = path;
        }

        Tex() {
            filename = name().replace("_", "").toLowerCase();
        }

        private static void load(TextureAtlas atlas) {
            for (Tex tex : values()) {
                tex.t = atlas.findRegion(tex.filename);
            }
        }
    }

    public static void load() {
        Tex.load(new TextureAtlas(Gdx.files.internal("textures/game.atlas")));
    }
}
