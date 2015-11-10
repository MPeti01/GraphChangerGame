package tungus.games.graphchanger.menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import tungus.games.graphchanger.BaseScreen;
import tungus.games.graphchanger.GraphChanger;

public class MainMenu extends BaseScreen {

    public MainMenu(final Game game) {
        super(game);
        Gdx.app.log("FLOW", "Main menu screen entered");
        Gdx.input.setInputProcessor(new InputAdapter(){

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (screenY > Gdx.graphics.getHeight() / 2) {
                    ((GraphChanger)game).setScreen(GraphChanger.mpConnectScreen);
                } else {
                    game.setScreen(new SinglePlayerSetup(game));
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
