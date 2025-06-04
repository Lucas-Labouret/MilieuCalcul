package local.misc.simulatedAnnealing;

@FunctionalInterface
public interface RandomNeighborGenerator<C, E> {
    /**
     * Generates a random neighbor solution based on the given candidate solution and environment.
     * This method is used to explore the solution space by perturbing the candidate solution
     * @param candidate the candidate solution to perturb
     * @param environment the environment in which the candidate solution exists
     * @return a new candidate solution that is a neighbor of the input candidate
     */
    C generate(C candidate, E environment);
}
