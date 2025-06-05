package local.computingMedia.cannings.simulatedAnnealing;

import local.computingMedia.cannings.Canning;
import local.computingMedia.cannings.VertexCanningCompleter;
import local.computingMedia.cannings.evaluation.MasksComputer;
import local.computingMedia.cannings.vertexCannings.VertexCanning;
import local.computingMedia.media.Medium;
import local.misc.simulatedAnnealing.Evaluator;

/**
 * Evaluator for vertex cannings that computes the average of the masks used for transfers.
 * The result is negated to minimize the average value.
 */
public class EverageMaskEvaluator implements Evaluator<VertexCanning> {
    @Override
    public double evaluate(VertexCanning candidate) {
        Canning canning = new VertexCanningCompleter(candidate);
        canning.can();

        MasksComputer masksComputer = new MasksComputer(canning);

        return (masksComputer.getAverageEfFe()+
                masksComputer.getAverageVeEv()+
                masksComputer.getAverageVfFv()) / -3 * candidate.getMedium().size();
    }
}
