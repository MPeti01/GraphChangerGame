package tungus.games.graphchanger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Stores and loads the assets (textures, sounds etc) needed in static members.
 */
public class Assets {
    public static enum Tex {
        NODE0, NODE1, NODE2, NODE_SELECTED, LINE, UNIT1, UNIT2;

        public static final Tex[] NODES = new Tex[]{NODE1, NODE2};
        public static final Tex[] UNITS = new Tex[]{UNIT1, UNIT2};

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
