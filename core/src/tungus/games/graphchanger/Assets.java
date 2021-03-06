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
        LINE, SPOT, UNIT1, UNIT2;

        public static final Tex[] UNITS = new Tex[]{UNIT1, UNIT2};

        private final String filename;
        public TextureRegion t = null;

        Tex() {
            filename = name().replace("_", "").toLowerCase();
        }

        private static void load() {
            for (Tex tex : values()) {
                tex.t = atlas.findRegion(tex.filename);
                if (tex.t == null) {
                    throw new RuntimeException("Texture not found: " + tex.filename);
                }
            }
        }
    }

    public static TextureAtlas atlas;
    public static BitmapFont font;

    // A member of a special subclass. Can't clone ParticleEffects as usual because it would create
    // ordinary ParticleEmitters.
    public static ParticleEmitter edgeEmitter;

    // Needs DistanceParticleEmitters, so each emitter should be copied separately
    public static ParticleEffect node;

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
        node = new ParticleEffect();
        node.load(Gdx.files.internal("particles/node"), Assets.atlas);
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
