package tungus.games.graphchanger.game.network;

import tungus.games.graphchanger.game.graph.editor.Move;

/**
 * Encodes a {@link Move} to a 4-byte int and decodes ints that were encoded this way.
 */
class MoveEncoder {
    public int encode(Move m) {
        if (m == null)
            return 0;
        int x = 0;
        x += m.node1ID;
        x <<= 15;
        x += m.node2ID;
        if (!m.add)
            x *= -1;
        return x;
    }

    public Move decode(int x) {
        if (x == 0)
            return null;
        boolean add = true;
        if (x < 0) {
            add = false;
            x *= -1;
        }
        int n1 = x >> 15;
        int n2 = (x - (n1 << 15));
        return new Move(n1, n2, add);
    }
}
