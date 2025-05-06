package local.computingMedia.cannings.simulatedAnnealing;

import local.computingMedia.cannings.vertexCannings.VertexCanning;
import local.computingMedia.media.Medium;
import local.misc.simulatedAnnealing.Annealer;
import local.misc.simulatedAnnealing.StandardAcceptor;
import local.misc.simulatedAnnealing.StandardHeatRegulator;

public class VertexCanningAnnealer extends Annealer<VertexCanning, Medium> {
    public VertexCanningAnnealer(int maxIterations) {
        super(
                maxIterations,
                new StandardHeatRegulator(maxIterations),
                new EverageMaskEvaluator(),
                new StandardAcceptor(),
                new NearestNeighborGenerator()
        );
    }
}
