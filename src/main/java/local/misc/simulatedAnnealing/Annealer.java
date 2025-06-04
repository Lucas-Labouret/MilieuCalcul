package local.misc.simulatedAnnealing;

/**
 * Abstract class representing an annealer for simulated annealing optimization.
 * This class defines the structure for an annealer that can optimize a solution
 * @param <C> the type of the candidate solution
 */
public abstract class Annealer<C> {
    protected TemperatureRegulator temperatureRegulator;
    protected Acceptor acceptor;
    protected Evaluator<C> evaluator;
    protected RandomNeighborGenerator<C> randomNeighborGenerator;

    public abstract C optimize(C seed);
}
