package local.computingMedia.cannings.simulatedAnnealing;

import local.computingMedia.cannings.Canning;

@FunctionalInterface
public interface Evaluator {
    double evaluate(Canning canning);
}
