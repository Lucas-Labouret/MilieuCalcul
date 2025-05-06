package local.misc.simulatedAnnealing;

@FunctionalInterface
public interface RandomNeighborGenerator<C, E> {
    C generate(C candidate, E environment);
}
