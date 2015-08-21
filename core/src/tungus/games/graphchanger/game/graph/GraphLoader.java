package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.graph.node.Node;
import tungus.games.graphchanger.game.players.Player;

import java.util.*;

/**
 * Loads the {@link tungus.games.graphchanger.game.graph.node.Node Nodes} and {@link Edge Edges} for a {@link Graph} from a file.
 */
public class GraphLoader {

    private final FileHandle file;
    public List<Node> nodes;
    public List<Edge> edges;
    public List<PartialEdge> partialEdges;


    public GraphLoader(FileHandle file) {
        this.file = file;
    }

    public void load(EdgePricer pricer) {
        nodes = new ArrayList<Node>();
        edges = new LinkedList<Edge>();
        partialEdges = new LinkedList<PartialEdge>();
        Scanner sc = new Scanner(file.read());
        sc.useLocale(Locale.US);
        while (sc.hasNext()) {
            int p = sc.nextInt();
            if (p == 0)
                nodes.add(new Node(new Vector2(sc.nextFloat(), sc.nextFloat()), nodes.size(), nodes, edges, pricer, partialEdges));
            else
                nodes.add(new Node(Player.values()[p-1], new Vector2(sc.nextFloat(), sc.nextFloat()), nodes.size(), nodes, edges, pricer, partialEdges));
        }
    }

    public void duplicate(EdgePricer pricer) {
        List<Node> newNodes = new ArrayList<Node>();
        List<Edge> newEdges = new ArrayList<Edge>();
        partialEdges = new LinkedList<PartialEdge>();
        for (Node n : nodes) {
            newNodes.add(new Node(n, newNodes, newEdges, pricer, partialEdges));
        }
        nodes = newNodes;
        edges = newEdges;
    }
}
