package tungus.games.graphchanger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.graphchanger.game.GameScreen;
import tungus.games.graphchanger.game.players.Player;

import java.util.Scanner;

public class NetMPScreen extends BaseScreen {
	
	private static final int MODE_LISTEN = 1;
	private static final int MODE_CONNECT = 2;
	
	private static int mode = MODE_LISTEN;
	private static int port = 8901;
	private static String IP = "25.???.??.???";
	
	public NetMPScreen(Game game) {
		super(game);
		try {
			Scanner sc = new Scanner(Gdx.files.internal("mpdebug.txt").read());
			String str = sc.next();
			if (str.equals("listen")) {
				mode = MODE_LISTEN;
			} else if (str.equals("connect")) {
				mode = MODE_CONNECT;
				IP = sc.next();
			} else {
				sc.close();
				return;
			}
			port = sc.nextInt();
			sc.close();
		} catch (Exception e) {
			Gdx.app.log("Net MP", "Failed to load MP command file");
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(float deltaTime) {
		Socket s;
		if (mode == MODE_CONNECT) {
			Gdx.app.log("MODE", "CONNECT");
			s = Gdx.net.newClientSocket(Net.Protocol.TCP, IP, port, new SocketHints());
			game.setScreen(new GameScreen(game, Player.P2, s.getInputStream(), s.getOutputStream()));
		} else if (mode == MODE_LISTEN) {
			Gdx.app.log("MODE", "LISTEN");
			try {
				ServerSocketHints hints = new ServerSocketHints();
				hints.acceptTimeout = 0;
				ServerSocket ss = Gdx.net.newServerSocket(Net.Protocol.TCP, port, hints);
				s = ss.accept(new SocketHints());
				ss.dispose();
				game.setScreen(new GameScreen(game, Player.P1, s.getInputStream(), s.getOutputStream()));
			} catch (GdxRuntimeException e) {
				Gdx.app.log("Net MP", "Socket accept timed out. Retrying...");
				e.printStackTrace();
			}
			
		}
	}

}
