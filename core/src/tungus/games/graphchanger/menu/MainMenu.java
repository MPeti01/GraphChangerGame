package tungus.games.graphchanger.menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import tungus.games.graphchanger.BaseScreen;
import tungus.games.graphchanger.GraphChanger;

public class MainMenu extends BaseScreen {

    public MainMenu(final Game game) {
        super(game);
        Gdx.app.log("MENU", "Main menu screen entered");
        Gdx.input.setInputProcessor(new InputAdapter(){
            private final Class<? extends Screen> mpScreen = GraphChanger.mpScreen;
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (screenY > Gdx.graphics.getHeight() / 2) {
                    Gdx.app.log("MENU", "Entering MP screen...");
                    ((GraphChanger)game).setScreen(mpScreen);
                } else {
                    // TODO Singleplayer...
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {

    }
}
