package local.computingMedia.cannings.simulatedAnnealing;

import local.computingMedia.cannings.vertexCannings.VertexCanning;
import local.computingMedia.media.Medium;
import local.misc.simulatedAnnealing.StandardAnnealer;
import local.misc.simulatedAnnealing.StandardAcceptor;
import local.misc.simulatedAnnealing.StandardHeatRegulator;

public class VertexCanningNearestNeighborAnnealer extends StandardAnnealer<VertexCanning, Medium> {
    public VertexCanningNearestNeighborAnnealer(int maxIterations) {
        super(
                maxIterations,
                new StandardHeatRegulator(maxIterations),
                new EverageMaskEvaluator(),
                new StandardAcceptor(),
                new NearestNeighborGenerator()
        );
    }
}
