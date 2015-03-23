package elude.games.graphchanger.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import elude.games.graphchanger.BasicTouchListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Peti on 2015.03.19..
 */
public class Graph {

    private final List<Node> nodes = new ArrayList<Node>();
    private final List<Edge> edges = new LinkedList<Edge>();
    private final GraphEditor editor = new GraphEditor(edges, nodes);
    private final GraphRenderer renderer = new GraphRenderer();

    public Graph(String s, Army[] armies) {
        nodes.add(new Node(new Vector2(30, 30)));
        nodes.add(new Node(new Vector2(30, 100)));
        nodes.add(new Node(new Vector2(30, 200)));
        nodes.add(new Node(new Vector2(30, 300)));
        nodes.add(new Node(new Vector2(30, 400)));
        nodes.add(new Node(new Vector2(200, 200)));
        nodes.add(new Node(new Vector2(300, 500)));
        nodes.add(new Node(new Vector2(300, 450)));
        nodes.add(new Node(new Vector2(250, 320)));
        nodes.add(new Node(new Vector2(50, 450)));

        edges.add(new Edge(nodes.get(0), nodes.get(1)));
        nodes.get(0).addNeighbor(nodes.get(1));
        nodes.get(1).addNeighbor(nodes.get(0));

        edges.add(new Edge(nodes.get(1), nodes.get(2)));
        nodes.get(2).addNeighbor(nodes.get(1));
        nodes.get(1).addNeighbor(nodes.get(2));

        edges.add(new Edge(nodes.get(3), nodes.get(4)));
        nodes.get(4).addNeighbor(nodes.get(3));
        nodes.get(3).addNeighbor(nodes.get(4));

        nodes.get(0).owner = armies[0];

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

}
