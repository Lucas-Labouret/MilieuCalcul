package local.computingMedia.cannings.simulatedAnnealing;

import local.computingMedia.cannings.Canning;

public class Annealer {
    int maxIterations;
    HeatRegulator heatRegulator;
    Evaluator evaluator;
    Acceptor acceptor;
    RandomNeighborGenerator randomNeighborGenerator;

    public Annealer(
            int maxIterations,
            HeatRegulator heatRegulator,
            Evaluator evaluator,
            Acceptor acceptor,
            RandomNeighborGenerator randomNeighborGenerator
    ) {
        this.maxIterations = maxIterations;
        this.heatRegulator = heatRegulator;
        this.evaluator = evaluator;
        this.acceptor = acceptor;
        this.randomNeighborGenerator = randomNeighborGenerator;
    }

    public Canning optimize(Canning seed) {
        Canning canning = seed;
        double oldScore;
        double newScore = evaluator.evaluate(canning);
        for (int i = 0; i < maxIterations; i++) {
            double heat = heatRegulator.progress();
            Canning newNeighbor = randomNeighborGenerator.generate(canning);
            oldScore = newScore;
            newScore = evaluator.evaluate(newNeighbor);
            if (acceptor.accept(oldScore, newScore, heat)) canning = newNeighbor;
        }
        return canning;
    }
}
