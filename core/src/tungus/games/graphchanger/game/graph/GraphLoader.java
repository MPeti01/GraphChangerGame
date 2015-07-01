package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.graph.node.Node;
import tungus.games.graphchanger.game.players.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Loads the {@link tungus.games.graphchanger.game.graph.node.Node Nodes} and {@link Edge Edges} for a {@link Graph} from a file.
 */
public class GraphLoader {

    private final FileHandle file;
    public List<Node> nodes;
    public List<Edge> edges;


    public GraphLoader(FileHandle file) {
        this.file = file;
    }

    public void load() {
        nodes = new ArrayList<Node>();
        edges = new ArrayList<Edge>();
        Scanner sc = new Scanner(file.read());
        sc.useLocale(Locale.US);
        while (sc.hasNext()) {
            int p = sc.nextInt();
            if (p == 0)
                nodes.add(new Node(new Vector2(sc.nextFloat(), sc.nextFloat()), nodes.size(), nodes));
            else
                nodes.add(new Node(Player.values()[p-1], new Vector2(sc.nextFloat(), sc.nextFloat()), nodes.size(), nodes));
        }
    }
}
