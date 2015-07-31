package tungus.games.graphchanger.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import tungus.games.graphchanger.GraphChanger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("WeakerAccess")
public class DesktopLauncher implements ActionListener{

    @Override
    public void actionPerformed(ActionEvent ev){
        jf.setVisible(false);
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 480;
        config.height = 800;
        NetMPScreen.IP = jtf_host.getText();
        NetMPScreen.port = Integer.valueOf(jtf_port.getText());
        new LwjglApplication(new GraphChanger(), config);
    }

    JFrame jf = new JFrame("Graph Changer");
    JButton jb = new JButton("Start");
    JTextField jtf_port = new JTextField("12345", 4);
    JTextField jtf_host = new JTextField("localhost", 10);

    public DesktopLauncher(){
        jb.addActionListener(this);
        jf.setLayout(new FlowLayout());
        jf.setBounds(100,100,500,90);
        jf.add(new JLabel("Leave host empty to create server"));
        jf.add(jtf_host);
        jf.add(jtf_port);
        jf.add(jb);
        jf.setVisible(true);
        GraphChanger.mpScreen = NetMPScreen.class;
    }

    public static void main(String[] arg) {
        new DesktopLauncher();
    }
}
