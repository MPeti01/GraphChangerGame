package tungus.games.graphchanger.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import tungus.games.graphchanger.GraphChanger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

@SuppressWarnings("WeakerAccess")
public class DesktopLauncher implements ActionListener{

    @Override
    public void actionPerformed(ActionEvent ev){
        jf.setVisible(false);
        writeFile(jtf_host.getText());
        startGame(jtf_host.getText(), jtf_port.getText());
    }

    JFrame jf = new JFrame("Graph Changer");

    JButton jb = new JButton("Start");
    JTextField jtf_port = new JTextField("12345", 4);
    JTextField jtf_host = new JTextField(readFile(), 10);

    public DesktopLauncher() {
        jb.addActionListener(this);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setLayout(new FlowLayout());
        jf.setBounds(100, 100, 500, 90);
        jf.add(new JLabel("Leave host empty to create server"));
        jf.add(jtf_host);
        jf.add(jtf_port);
        jf.add(jb);
        jf.setVisible(true);
    }

    private String readFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("lastconnection.txt"));
            String str = reader.readLine();
            reader.close();
            return str;
        } catch (java.io.IOException e) {
            return "";
        }
    }

    private void writeFile(String str) {
        try {
            PrintWriter writer = new PrintWriter("lastconnection.txt", "UTF-8");
            writer.println(str);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void startGame() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 480;
        config.height = 800;
        GraphChanger.mpScreen = NetMPScreen.class;
        new LwjglApplication(new GraphChanger(), config);
    }

    private static void startGame(String IP, String port) {
        NetMPScreen.IP = IP;
        NetMPScreen.port = Integer.valueOf(port);
        startGame();
    }

    public static void main(String[] arg) {
        if (arg.length == 0)
            new DesktopLauncher();
        else {
            startGame(arg[0], arg[1]);
        }
    }
}
