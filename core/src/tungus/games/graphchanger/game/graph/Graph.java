package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.BasicTouchListener;
import tungus.games.graphchanger.game.players.Army;
import tungus.games.graphchanger.game.players.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Peti on 2015.03.19..
 */
public class Graph implements NodeList {

    private final List<Node> nodes = new ArrayList<Node>();
    private final List<Edge> edges = new LinkedList<Edge>();
    private final GraphEditor editor = new GraphEditor(edges, nodes, new MoveValidator(Player.P1));
    private final GraphRenderer renderer = new GraphRenderer();

    public Graph(@SuppressWarnings({"SameParameterValue", "UnusedParameters"}) String s, Army[] armies) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                nodes.add(new Node(new Vector2(30 + i * 80, 30 + j * 120), j * 6 + i, this));
            }
        }
        nodes.add(new Node(armies[0], new Vector2(230, 450), 43, this));

        edges.add(new Edge(nodes.get(0), nodes.get(10)));
        nodes.get(0).addNeighbor(nodes.get(10));
        nodes.get(10).addNeighbor(nodes.get(0));
    }

    public BasicTouchListener getEditorInput() {
        return editor.input;
    }

    public void updateNodes(float delta) {
        for (Node n : nodes) {
            n.update(delta);
        }
    }

    public void render(SpriteBatch batch) {
        renderer.drawEdges(edges, editor, batch);
        editor.render(batch);
        renderer.drawNodes(nodes, editor, batch);
    }

    public void set(Graph other) {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).set(other.nodes.get(i));
        }
        setEdges(other.edges);
    }

    private void setEdges(List<Edge> other) {
        Iterator<Edge> it = edges.iterator();
        while (it.hasNext()) {
            Edge here = it.next();
            if (!other.contains(here)) {
                it.remove();
            }
        }
        for (Edge there : other) {
            if (!edges.contains(there)) {
                edges.add(new Edge(nodes.get(there.node1.id), nodes.get(there.node2.id)));
            }
        }
    }

    @Override
    public Node get(int id) {
        return nodes.get(id);
    }
}