package local.computingMedia.cannings.vertexCannings;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.Vertex;
import local.misc.simulatedAnnealing.Annealer;

import java.util.HashMap;

public class VertexCanningAnnealer implements VertexCanning {
    private Medium medium;
    private final VertexCanning baseCanning;
    private VertexCanning annealedCanning;
    private final Annealer<VertexCanning, Medium> annealer;

    public VertexCanningAnnealer(VertexCanning baseCanning, Annealer<VertexCanning, Medium> annealer) {
        this.baseCanning = baseCanning;
        this.annealer = annealer;
    }

    @Override
    public void setMedium(Medium medium) {
        this.medium = medium;
        baseCanning.setMedium(medium);
    }

    @Override
    public void can() {
        baseCanning.can();
        annealedCanning = annealer.optimize(baseCanning, medium);
    }

    @Override public HashMap<Vertex, VertexCoord> getVertexCanning() {
        return annealedCanning.getVertexCanning();
    }

    @Override public int getWidth() {
        return annealedCanning.getWidth();
    }
    @Override public int getHeight() {
        return annealedCanning.getHeight();
    }
}
