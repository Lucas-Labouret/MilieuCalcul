package local.computingMedia.cannings.vertexCannings;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.Vertex;
import local.misc.simulatedAnnealing.Annealer;

import java.util.HashMap;

/**
 * VertexCanningAnnealer is a vertex canning implementation that uses simulated annealing to optimize the canning of vertices.
 * It takes a base vertex canning and applies the annealing process to find an optimal configuration.
 */
public class VertexCanningAnnealer implements VertexCanning {
    private final Medium medium;
    private final VertexCanning baseCanning;
    private VertexCanning annealedCanning;
    private final Annealer<VertexCanning> annealer;

    /**
     * @param baseCanning the base vertex canning to be optimized. Its "can()" method need not be called before using this class.
     * @param annealer the annealer to be used for optimizing the vertex canning.
     */
    public VertexCanningAnnealer(VertexCanning baseCanning, Annealer<VertexCanning> annealer) {
        this.medium = baseCanning.getMedium();
        this.baseCanning = baseCanning;
        this.annealer = annealer;
    }

    @Override public Medium getMedium() { return medium; }

    @Override public int getWidth() {
        return annealedCanning.getWidth();
    }
    @Override public int getHeight() {
        return annealedCanning.getHeight();
    }

    @Override
    public void can() {
        baseCanning.can();
        annealedCanning = annealer.optimize(baseCanning);
    }

    @Override public HashMap<Vertex, VertexCoord> getVertexCanning() {
        return annealedCanning.getVertexCanning();
    }
}
