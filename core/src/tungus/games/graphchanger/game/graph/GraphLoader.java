package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.players.Army;

import java.util.*;

/**
 * Loads the {@link Node Nodes} and {@link Edge Edges} for a {@link Graph} from a file.
 */
public class GraphLoader {

    private final FileHandle file;
    public final List<Node> nodes = new ArrayList<Node>();
    public final List<Edge> edges = new LinkedList<Edge>();


    public GraphLoader(FileHandle file) {
        this.file = file;
    }

    public void load(Army... armies) {
        nodes.clear();
        edges.clear();
        Scanner sc = new Scanner(file.read());
        sc.useLocale(Locale.US);
        while (sc.hasNext()) {
            int p = sc.nextInt();
            if (p == 0)
                nodes.add(new Node(new Vector2(sc.nextFloat(), sc.nextFloat()), nodes.size(), nodes));
            else
                nodes.add(new Node(armies[p-1], new Vector2(sc.nextFloat(), sc.nextFloat()), nodes.size(), nodes));
        }
    }
}
