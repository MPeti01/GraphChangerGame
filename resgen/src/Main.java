import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Created by Peti on 2015.03.19..
 */
@SuppressWarnings("WeakerAccess")
public class Main {
    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 1024;
        settings.maxHeight = 1024;
        settings.pot = true;
        TexturePacker.process(settings, "img/done", "android/assets/textures", "game");
    }
}
