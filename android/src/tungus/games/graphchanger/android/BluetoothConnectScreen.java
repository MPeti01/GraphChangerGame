package tungus.games.graphchanger.android;

import android.bluetooth.BluetoothDevice;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import tungus.games.graphchanger.BaseScreen;
import tungus.games.graphchanger.android.BluetoothConnector.Client;
import tungus.games.graphchanger.android.BluetoothConnector.ClientState;
import tungus.games.graphchanger.android.BluetoothConnector.Server;
import tungus.games.graphchanger.game.GameScreen;
import tungus.games.graphchanger.game.players.Player;

import java.util.LinkedList;
import java.util.List;

public class BluetoothConnectScreen extends BaseScreen {
	
	private static final int levelNum = 4;
	
	private final Client client;
	private final Server server;
	
	private boolean serverReady = false;
	private boolean clientReady = false;
	
	private final BTListUI gui = new BTListUI();
	private List<BluetoothDevice> devices = new LinkedList<BluetoothDevice>();
	
	private InputAdapter listener = new InputAdapter() {
		
		@Override
		public boolean touchDown (int screenX, int screenY, int pointer, int button) {
			int n = gui.tapSelection(screenX, screenY);
			if (n != -1) {
				client.connectTo(devices.get(n));
			}
			return false;
		}
	};
	
	public BluetoothConnectScreen(Game game) {
		super(game);
		BluetoothConnector.INSTANCE.enable();
		server = BluetoothConnector.INSTANCE.server;
		client = BluetoothConnector.INSTANCE.client;
		Gdx.input.setInputProcessor(listener);
	}
	
	@Override
	public void render(float deltaTime) {
		updateServer();
		updateClient();
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (client.state != ClientState.DISCOVERING) {
			gui.renderMessage("LOADING");
		} else {
			gui.renderList(devices);
		}
	}
	
	public void updateServer() {
		switch (server.state) {
		case ERROR:
			game.setScreen(new BluetoothConnectScreen(game));
			break;
		case ENABLED:
			if (!serverReady) {
				server.acceptThread = server.new AcceptThread();
				server.acceptThread.setName("Accept thread");
				server.acceptThread.start();
				server.enableVisibility();
				serverReady = true;		
			}
			break;
		case CONNECTED:
			game.setScreen(new GameScreen(game, Player.P1, BluetoothConnector.INSTANCE.in, BluetoothConnector.INSTANCE.out));
			break;
		default:
			break;
		}
	}
	
	public void updateClient() {
		switch (client.state) {
		case ERROR:
            game.setScreen(new BluetoothConnectScreen(game));
			break;
		case ENABLED:
			if (!clientReady) {
				Gdx.app.log("Bluetooth", "Starting discovery...");
				client.enableDiscovery();
				clientReady = true;				
			}
			break;
		case CONNECTED:
            game.setScreen(new GameScreen(game, Player.P2, BluetoothConnector.INSTANCE.in, BluetoothConnector.INSTANCE.out));
			break;
		case DISCOVERING:
			client.listDevices(devices);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void hide() {
		if (server.acceptThread != null && server.acceptThread.isAlive())
			server.acceptThread.cancel();
		if (client.connectThread != null && client.connectThread.isAlive())
			client.connectThread.cancel();
		client.disableDiscovery();
	}
	
}