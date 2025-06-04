package local.misc;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * A collection that allows adding elements with weights and retrieving them randomly based on their weights.
 * The higher the weight, the more likely the element is to be selected.
 *
 * @param <E> the type of elements in this collection
 */
public class WeightedRandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<>();
    private final Random random = new Random();
    private double total = 0;

    /**
     * Adds an element with a specified weight to the collection.
     *
     * @param weight the weight of the element, must be greater than 0
     * @param result the element to add
     * @return this collection for method chaining
     */
    public WeightedRandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    /** @return a random element from the collection. */
    public E next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }

    /** Removes all elements from the collection. */
    public void clear() {
        map.clear();
        total = 0;
    }

    /** @return the size of the collection. */
    public int size() {
        return map.size();
    }
}


