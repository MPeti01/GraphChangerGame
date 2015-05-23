package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tungus.games.graphchanger.game.editor.GraphEditor;
import tungus.games.graphchanger.game.editor.Move;
import tungus.games.graphchanger.game.players.Army;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class representing the game's "map" or graph. Handles/delegates loading from file, updating, rendering.
 * Can copy data from another instance.
 */
public class Graph implements NodeList {

    final List<Node> nodes = new ArrayList<Node>();
    final List<Edge> edges = new LinkedList<Edge>();
    private final GraphEditor editor;
    private final GraphRenderer renderer = new GraphRenderer();

    public Graph(@SuppressWarnings({"SameParameterValue", "UnusedParameters"}) GraphEditor editor, String s, Army... armies) {
        this.editor = editor;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (i == 0 && j == 0)
                    nodes.add(new Node(armies[1], new Vector2(30 + i * 80, 30 + j * 120), i * 7 + j, this));
                else
                    nodes.add(new Node(new Vector2(30 + i * 80, 30 + j * 120), i * 7 + j, this));
            }
        }
        nodes.add(new Node(armies[0], new Vector2(230, 450), 42, this));

        edges.add(new Edge(nodes.get(0), nodes.get(10)));
        nodes.get(0).addNeighbor(nodes.get(10));
        nodes.get(10).addNeighbor(nodes.get(0));
    }

    public void updateNodes(float delta) {
        for (Node n : nodes) {
            n.update(delta);
        }
    }

    public void render(SpriteBatch batch, float sinceTick) {
        renderer.drawEdges(edges, editor, batch);
        editor.render(batch);
        renderer.drawNodes(nodes, editor, batch);
    }

    public void set(Graph other, Army p1, Army p2) {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).set(other.nodes.get(i), p1, p2);
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

    public void applyMove(Move m) {
        Node n1 = nodes.get(m.node1ID);
        Node n2 = nodes.get(m.node2ID);
        if (m.add) {
            n1.addNeighbor(n2);
            n2.addNeighbor(n1);
            edges.add(new Edge(n1, n2));
        } else {
            n1.removeNeighbor(n2);
            n2.removeNeighbor(n1);
            edges.remove(new Edge(n1, n2));
        }
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}