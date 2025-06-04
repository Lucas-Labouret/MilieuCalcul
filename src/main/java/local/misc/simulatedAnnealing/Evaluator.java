package local.misc.simulatedAnnealing;

@FunctionalInterface
public interface Evaluator<C, E> {
    /**
     * Evaluates the candidate solution in the given environment.
     * @param candidate the candidate solution to evaluate
     * @param environment the environment in which the candidate solution is evaluated
     * @return the score of the candidate solution, higher is better
     */
    double evaluate(C candidate, E environment);
}
