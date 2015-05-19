package tungus.games.graphchanger.game.graph;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Peti on 2015.03.22..
 */
class EdgeIntersector {
    private static final Vector2 intersection = new Vector2();
    private final List<Edge> edges;
    private final List<Edge> intersecting = new LinkedList<Edge>();

    EdgeIntersector(List<Edge> edges) {
        this.edges = edges;
    }

    void updateFor(Vector2 start, Vector2 end) {
        intersecting.clear();
        for (Edge edge : edges) {
            if (Intersector.intersectSegments(start, end, edge.v1, edge.v2, intersection)) {
                if (intersection.dst2(end) > 1 && intersection.dst2(start) > 1) {
                    intersecting.add(edge);
                }
            }
        }
    }

    int cutCount() {
        return intersecting.size();
    }

    Edge cutEdge() {
        if (intersecting.size() != 1)
            throw new IllegalStateException("Getting cut edge when there are " + intersecting.size());
        return intersecting.get(0);
    }

}
