package local.simulatedAnnealing.neighborGenerator;

@FunctionalInterface
public interface NeighborGenerator<C> {
    /**
     * Generates a random neighbor solution based on the given candidate solution.
     * This method is used to explore the solution space by perturbing the candidate solution
     * @param candidate the candidate solution to perturb
     * @return a new candidate solution that is a neighbor of the input candidate
     */
    C generate(C candidate);
}
