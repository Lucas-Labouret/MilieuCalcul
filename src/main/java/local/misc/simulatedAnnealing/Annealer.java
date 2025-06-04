package local.misc.simulatedAnnealing;

/**
 * Abstract class representing an annealer for simulated annealing optimization.
 * This class defines the structure for an annealer that can optimize a solution
 * @param <C> the type of the candidate solution
 * @param <E> the type of the environment in which the optimization occurs
 */
public abstract class Annealer<C, E> {
    protected TemperatureRegulator temperatureRegulator;
    protected Acceptor acceptor;
    protected Evaluator<C, E> evaluator;
    protected RandomNeighborGenerator<C, E> randomNeighborGenerator;

    public abstract C optimize(C seed, E environment);
}
