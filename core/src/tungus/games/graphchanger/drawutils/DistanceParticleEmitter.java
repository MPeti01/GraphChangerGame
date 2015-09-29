package tungus.games.graphchanger.drawutils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A modified ParticleEmitter. The life of the Particles it creates is set so that they always reach the distance given.
 * The life values from the file read are ignored, and are instead calculated from the velocity and the distance.
 */
public class DistanceParticleEmitter extends ParticleEmitter {
    private float goalDistance;

    public DistanceParticleEmitter(FileHandle file, float dist) throws IOException {
        super(new BufferedReader(new InputStreamReader(file.read())));
        goalDistance = dist;
    }

    public DistanceParticleEmitter(DistanceParticleEmitter other) {
        super(other);
        goalDistance = other.goalDistance;
    }

    @Override
    protected void activateParticle(int i) {
        super.activateParticle(i);
        if (particles[i].velocity != 0 || particles[i].velocityDiff != 0) {
            particles[i].currentLife = particles[i].life =
                    (int)(goalDistance / (particles[i].velocity + particles[i].velocityDiff) * 1000);
        }
    }

    public void setDistance(float distance) {
        this.goalDistance = distance;
    }
}
