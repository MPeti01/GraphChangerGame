package tungus.games.graphchanger.game.network;

import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Writes 32 bit integers to OutputStreams and reads them from InputStreams. <br>
 * (The streams' methods only support writing bytes.)
 */
public class IntReaderWriter {
    public int read(InputStream in) {
        try {
            int x = in.read();
            for (int i = 0; i < 3; i++)
            {
                x <<= 8;
                x |= in.read();
            }
            return x;
        } catch (IOException e) {
            throw new GdxRuntimeException("Failed to read from InputStream", e);
        }
    }

    public void write(OutputStream out, int x) {
        try {
            for (int i = 3; i >= 0; i--) {
                out.write(x >> (8*i));
            }
        } catch (IOException e) {
            throw new GdxRuntimeException("Failed to read from InputStream", e);
        }

    }
}
