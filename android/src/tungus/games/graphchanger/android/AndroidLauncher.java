package tungus.games.graphchanger.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import tungus.games.graphchanger.GraphChanger;

@SuppressWarnings("WeakerAccess")
public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        GraphChanger.mpConnectScreen = BluetoothConnectScreen.class;
        BluetoothConnector.app = this;
        BTListUI.loc = getResources().getConfiguration().locale;
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new GraphChanger(), config);
	}
}
