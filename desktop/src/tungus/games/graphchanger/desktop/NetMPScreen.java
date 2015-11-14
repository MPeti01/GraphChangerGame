package tungus.games.graphchanger.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.BaseScreen;
import tungus.games.graphchanger.NetworkCommunicator;
import tungus.games.graphchanger.game.players.Player;
import tungus.games.graphchanger.menu.MultiPlayerSetup;

public class NetMPScreen extends BaseScreen {
	
	public static int port = 8901;
	public static String IP = "???.???.???.???";

	public NetMPScreen(Game game) {
		super(game);
        Gdx.app.log("SCREEN", "Entered internet MP connect screen");
		if(IP.equals("")) {
            Gdx.app.log("SCREEN", "Mode: listen");
            new Thread() {
                @Override
                public void run() {
                    while(!connected) {
                        try {
                            ServerSocketHints hints = new ServerSocketHints();
                            hints.acceptTimeout = 0;
                            ServerSocket ss = Gdx.net.newServerSocket(Net.Protocol.TCP, port, hints);
                            connection = ss.accept(new SocketHints());
                            ss.dispose();
                            player = Player.P1;
                            connected = true;
                        } catch (GdxRuntimeException e) {
                            Gdx.app.log("Net MP", "Socket accept timed out. Retrying...");
                        }
                    }
                }
            }.start();
        } else {
            Gdx.app.log("SCREEN", "Mode: connect");
            new Thread() {
                @Override
                public void run() {
                    while(!connected) {
                        try {
                            connection = Gdx.net.newClientSocket(Net.Protocol.TCP, IP, port, new SocketHints());
                            player = Player.P2;
                            connected = true;
                        } catch (GdxRuntimeException e) {
                            Gdx.app.log("NETWORK", "Failed to connect to " + IP + ". Retrying...");
                        }
                    }
                }
            }.start();
        }
	}

    private volatile boolean connected = false;
    private Socket connection = null;
    private Player player = null;
	
	@Override
	public void render(float deltaTime) {
        if (connected) {
            NetworkCommunicator comm = new NetworkCommunicator(connection.getInputStream(), connection.getOutputStream());
            game.setScreen(new MultiPlayerSetup(game, comm, player));
        }
	}

}
