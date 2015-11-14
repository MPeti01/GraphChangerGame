package tungus.games.graphchanger.game.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Active state before starting the game in multiplayer. Responsible for starting the game
 * at the same time on both clients.
 */
public class SyncState extends GameScreenState {

    /*
       The procedure of syncing:
       The non-initiating client (where isInitiator == false) signals when it's ready (at this state)
       by sending an empty message. When the initiating client is also here and has received the message,
       it sends a message to signal the start of the game.

       When the non-initiating client receives this, it sets the starting time to WAIT_TIME_MILLIS ms from
       this moment, then sends a confirmation message to the initiating client.

       The initiating client assumes that the other client received the start information halfway between
       this moment and when it was originally sent (i.e. the two messages took equal time to deliver).
       From this, it calculates when the other client is going to start the game and sets its own start
       time to the same.
     */

    private static final long WAIT_TIME_MILLIS = 100;

    /**
     * Whether this client signals the start time. Should always be true for exactly one
     * of the two connected clients.
     */
    private final boolean isInitiator;
    private volatile long gameStartTime; // When the game should be started
    private volatile long goMessageTime; // When the initiating client sent the start message
    /**
     * Whether the other client is ready to start. Always true for the non-initiating client.
     */
    private boolean remoteReady;


    public SyncState(GameScreen screen, boolean initiator) {
        super(screen);
        Gdx.app.log("FLOW", "Starting MP time sync, " + (initiator ? "initiating" : "not initiating") + "...");
        isInitiator = initiator;
        gameStartTime = goMessageTime = -1;
        remoteReady = !isInitiator;
    }

    @Override
    public void onEnter() {
        screen.comm.setDetailedLogging(true);
        if (!isInitiator) {
            screen.comm.write();
            Gdx.app.log("NETWORK", "Sent READY");
        }
    }

    @Override
    public GameScreenState render(SpriteBatch batch, float delta) {
        if (gameStartTime != -1) {
            return new CountDownState(screen, gameStartTime);
        } else {
            return this;
        }
    }

    @Override
    public boolean receivedMessage(int[] m) {
        if (!isInitiator) {
            Gdx.app.log("NETWORK", "Received GO, sent CONFIRM, starting game");
            gameStartTime = TimeUtils.millis() + WAIT_TIME_MILLIS;
            screen.comm.write();
        } else {
            if (!remoteReady) {
                Gdx.app.log("NETWORK", "Received READY, sending GO");
                remoteReady = true;
                goMessageTime = TimeUtils.millis();
                screen.comm.write();
            } else {
                Gdx.app.log("NETWORK", "Received CONFIRM, starting game");
                gameStartTime = (TimeUtils.millis() + goMessageTime) / 2 + WAIT_TIME_MILLIS;
            }
        }
        return true;
    }

}
