package local.misc.simulatedAnnealing;

@FunctionalInterface
public interface Evaluator<C, E> {
    double evaluate(C candidate, E environment);
}
