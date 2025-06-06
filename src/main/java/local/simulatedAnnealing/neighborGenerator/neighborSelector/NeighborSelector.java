package local.simulatedAnnealing.neighborGenerator.neighborSelector;

public interface NeighborSelector<E> {
    /**
     * Adds a neighbor to the collection.
     *
     * @param weight the weight of the neighbor
     * @param neighbor the neighbor to add
     * @return this collection for method chaining
     */
    NeighborSelector<E> add(double weight, E neighbor);

    /**
     * Retrieves a neighbor from the collection.
     * Leaves the selection to the implementation, which may use random selection, always return the first element, etc.
     */
    E next() throws IllegalStateException;

    /**
     * Clears all neighbors from the collection.
     */
    void clear();

    /**
     * Gets the size of the collection.
     *
     * @return the number of neighbors in the collection
     */
    int size();
}
