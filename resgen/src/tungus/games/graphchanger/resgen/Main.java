package tungus.games.graphchanger.resgen;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Generates assets required for the game: packed textures, levels
 */
@SuppressWarnings("WeakerAccess")
public class Main {
    public static void main(String[] args) {
        genRandomLevel("android/assets/levels/random1.lvl");
        genPerfTest();
        genTextures();
    }

    public static void genRandomLevel(String file) {
        LevelGenerator generator = new LevelGenerator(file);
        generator.addStartNodes(30, 450, 30, 770, 0.1f);
        generator.genRandomNodes(30, 30, 450, 30, 770, 80f);
        generator.write();
    }

    public static void genPerfTest() {
        LevelGenerator perfTest = new LevelGenerator("android/assets/levels/perftest.lvl");
        perfTest.genPerfTest();
        perfTest.write();
    }

    public static void genTextures() {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 1024;
        settings.maxHeight = 1024;
        settings.pot = true;
        TexturePacker.process(settings, "img/done", "android/assets/textures", "game");
    }
}
