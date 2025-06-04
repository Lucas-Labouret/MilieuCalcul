package local.misc.simulatedAnnealing;

@FunctionalInterface
public interface Evaluator<C> {
    /**
     * Evaluates the candidate solution.
     * @param candidate the candidate solution to evaluate
     * @return the score of the candidate solution, higher is better
     */
    double evaluate(C candidate);
}
