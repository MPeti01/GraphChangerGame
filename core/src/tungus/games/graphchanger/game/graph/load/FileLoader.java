package tungus.games.graphchanger.game.graph.load;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.graph.Edge;
import tungus.games.graphchanger.game.graph.PartialEdge;
import tungus.games.graphchanger.game.graph.node.Node;
import tungus.games.graphchanger.game.players.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Scanner;

/**
 * GraphLoader implementation that loads from a file.
 */
public class FileLoader extends GraphLoader {

    private final FileHandle file;

    public FileLoader(FileHandle file) {
        this.file = file;
    }

    @Override
    public void load() {
        nodes = new ArrayList<Node>();
        edges = new LinkedList<Edge>();
        partialEdges = new LinkedList<PartialEdge>();
        Scanner sc = new Scanner(file.read());
        sc.useLocale(Locale.US);
        while (sc.hasNext()) {
            int p = sc.nextInt();
            if (p == 0)
                newNode(new Vector2(sc.nextFloat(), sc.nextFloat()));
            else
                newNode(new Vector2(sc.nextFloat(), sc.nextFloat()), Player.values()[p - 1]);
        }
    }
}
