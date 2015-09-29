package tungus.games.graphchanger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.drawutils.DistanceParticleEmitter;

import java.io.IOException;

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

        Tex() {
            filename = name().replace("_", "").toLowerCase();
        }

        private static void load() {
            for (Tex tex : values()) {
                tex.t = atlas.findRegion(tex.filename);
            }
        }
    }

    public static TextureAtlas atlas;
    public static BitmapFont font;

    public static ParticleEffectPool node;

    // A member of a special subclass. Can't use ParticleEffect as usual because it would create
    // ordinary ParticleEmitters.
    public static ParticleEmitter edgeEmitter;


    public static void load() {
        atlas = new TextureAtlas(Gdx.files.internal("textures/game.atlas"));
        Tex.load();
        loadParticles();
        loadFont();
    }

    private static void loadFont() {
        Texture fontTex = new Texture(Gdx.files.internal("font/bulletproof.png"));
        fontTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion fontRegion = new TextureRegion(fontTex);
        font = new BitmapFont(Gdx.files.internal("font/bulletproof.fnt"), fontRegion);
    }

    private static void loadParticles() {
        ParticleEffect nodeEffect = new ParticleEffect();
        nodeEffect.load(Gdx.files.internal("particles/node"), Assets.atlas);
        node = new ParticleEffectPool(nodeEffect, 40, 100);

        try {
            edgeEmitter = new DistanceParticleEmitter(Gdx.files.internal("particles/edge"), 300);
            // Create an Effect just to load the damn Sprite.
            // (Can't copy the effect as usual because it would create ordinary ParticleEmitters).
            ParticleEffect wrapper = new ParticleEffect();
            wrapper.getEmitters().add(edgeEmitter);
            wrapper.loadEmitterImages(atlas);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GdxRuntimeException("Edge emitter not found");
        }
    }
}
