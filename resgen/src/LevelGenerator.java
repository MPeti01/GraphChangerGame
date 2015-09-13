import com.badlogic.gdx.math.Vector2;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Generates data for a level: where the nodes are, who their owners are
 */
public class LevelGenerator {

    private static class NodeData {
        Vector2 pos;
        int player, level;

        public NodeData(int player, float x, float y, int level) {
            this.pos = new Vector2(x, y);
            this.player = player;
            this.level = level;
        }
        @Override
        public String toString() {
            return player + " " + pos.x + " " + pos.y + " " + level;
        }
    }

    private static class EdgeData {
        int n1, n2;

        public EdgeData(int n1, int n2) {
            this.n1 = n1;
            this.n2 = n2;
        }

        @Override
        public String toString() {
            return n1 + " " + n2;
        }
    }
    private final PrintWriter out;
    private final Random rand;
    private final List<NodeData> nodes = new LinkedList<NodeData>();
    private final List<EdgeData> edges = new LinkedList<EdgeData>();

    public LevelGenerator(String file, Random rand) {
        this.rand = rand;
        try {
            out = new PrintWriter(file, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Can't write file", e);
        }
    }

    public LevelGenerator(String file) {
        this(file, new Random());
    }

    /**
     * Creates a node for each player in a given rectangle, each splitting the width/height of the rectangle by a given ratio.
     * @param minX The left of the rect
     * @param maxX The right of the rect
     * @param minY The bottom of the rect
     * @param maxY The top of the rect
     * @param split The ratio of the split. E.g. if 0.4, the nodes will be near the middle, close to each other.
     */
    public void addStartNodes(float minX, float maxX, float minY, float maxY, float split) {
        nodes.add(new NodeData(1, minX + split * (maxX - minX), minY + split * (maxY - minY), 0));
        nodes.add(new NodeData(2, maxX - split * (maxX - minX), maxY - split * (maxY - minY), 0));
    }

    /**
     * Generates random, neutral nodes in pairs (central symmetry) inside a rectangle.
     * @param nodeCount The number of nodes to generate
     * @param minX The left of the rect
     * @param maxX The right of the rect
     * @param minY The bottom of the rect
     * @param maxY The top of the rect
     * @param minDist The minimal distance between any two nodes (including already added ones). Will be rounded down if odd.
     */
    public void genRandomNodes(int nodeCount, float minX, float maxX, float minY, float maxY, float minDist) {
        float width = maxX - minX;
        float height = maxY - minY;
        Vector2 topRight = new Vector2(maxX, maxY);
        Vector2 bottomLeft = new Vector2(minX, minY);

        while (nodeCount > 0) {
            Vector2 p1 = new Vector2(rand.nextFloat()*width, rand.nextFloat()*height);
            Vector2 p2 = new Vector2(topRight).sub(p1);
            float minDist2 = minDist*minDist;
            p1.add(bottomLeft);
            if (p1.dst2(p2) < minDist2) {
                continue;
            }
            boolean allOk = true;
            for (NodeData node : nodes) {
                if (node.pos.dst2(p1) < minDist*minDist || node.pos.dst2(p2) < minDist2) {
                    allOk = false;
                }
            }
            if (!allOk) {
                continue;
            }
            nodes.add(new NodeData(0, p1.x, p1.y, 0));
            nodes.add(new NodeData(0, p2.x, p2.y, 0));
            nodeCount -= 2;
            System.out.println(nodeCount + " nodes left");
        }
    }

    public void write() {
        out.println(nodes.size());
        for (NodeData node : nodes) {
            out.println(node.toString());
        }
        out.println(edges.size());
        for (EdgeData edge : edges) {
            out.println(edge.toString());
        }
        out.close();
    }

    public void genPerfTest() {
        int nodeCount = 40;
        for (int i = 0; i < nodeCount / 2; i++) {
            nodes.add(new NodeData(1, 40 + (i % 5) * 100, 40 + (i / 5) * 100, 2));
        }
        for (int i = 0; i < nodeCount / 2; i++) {
            nodes.add(new NodeData(2, 440 - (i % 5) * 100, 760 - (i / 5) * 100, 2));
        }
        for (int i = 0; i < nodeCount / 2 - 1; i++) {
            edges.add(new EdgeData(i, i + 1));
            edges.add(new EdgeData(i + nodeCount / 2, i + nodeCount / 2 + 1));
        }
    }
}
