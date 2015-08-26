package tungus.games.graphchanger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Stores and loads the assets (textures, sounds etc) needed in static members.
 */
public class Assets {
    public static enum Tex {
        NODE0, NODE10, NODE20, NODE11, NODE21, NODE12, NODE22, NODE_SELECTED, LINE, UNIT1, UNIT2;

        public static final Tex[][] NODES = new Tex[][]{{NODE10, NODE11, NODE12}, {NODE20, NODE21, NODE22}};
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

    public static BitmapFont font;


    public static void load() {
        Tex.load(new TextureAtlas(Gdx.files.internal("textures/game.atlas")));
        loadFont();
    }

    private static void loadFont() {
        Texture fontTex = new Texture(Gdx.files.internal("font/bulletproof.png"));
        fontTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion fontRegion = new TextureRegion(fontTex);
        font = new BitmapFont(Gdx.files.internal("font/bulletproof.fnt"), fontRegion);
    }
}
