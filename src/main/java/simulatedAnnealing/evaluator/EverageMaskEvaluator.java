package simulatedAnnealing.evaluator;

import cannings.Canning;
import cannings.VertexCanningCompleter;
import cannings.evaluation.MasksComputer;
import cannings.vertexCannings.VertexCanning;

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
                masksComputer.getAverageVfFv()) * masksComputer.getDeltas()[2] / -3;
    }
}
