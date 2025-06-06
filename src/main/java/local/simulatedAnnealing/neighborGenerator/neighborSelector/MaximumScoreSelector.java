package local.simulatedAnnealing.neighborGenerator.neighborSelector;

import local.simulatedAnnealing.evaluator.Evaluator;

public class MaximumScoreSelector<E> implements  NeighborSelector<E> {
    private final Evaluator<E> evaluator;

    private double maximumScore;
    private E maximumNeighbor;
    int size = 0;

    public MaximumScoreSelector(Evaluator<E> evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public NeighborSelector<E> add(double weight, E neighbor) {
        double score = evaluator.evaluate(neighbor);
        if (score > maximumScore || size == 0) {
            maximumScore = score;
            maximumNeighbor = neighbor;
        }
        size++;
        return this;
    }

    @Override
    public E next() throws IllegalStateException {
        if (size == 0) throw new IllegalStateException("No elements available to select.");
        return maximumNeighbor;
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }
}
