package local.simulatedAnnealing.neighborGenerator.neighborSelector;

public class MaximumWeightSelector<E> implements NeighborSelector<E> {
    double maximum;
    E maximumNeighbor;
    int size = 0;

    @Override
    public NeighborSelector<E> add(double weight, E neighbor) {
        if (weight > maximum || size == 0) {
            maximum = weight;
            maximumNeighbor = neighbor;
        }
        size++;
        return this;
    }

    @Override
    public E next() {
        if (size == 0) throw new IllegalStateException("No elements available to select.");
        return maximumNeighbor;
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public int size() {
        return 0;
    }
}
