package local.simulatedAnnealing;

import local.simulatedAnnealing.acceptor.Acceptor;
import local.simulatedAnnealing.evaluator.Evaluator;
import local.simulatedAnnealing.neighborGenerator.NeighborGenerator;
import local.simulatedAnnealing.temperatureRegulator.TemperatureRegulator;

/**
 * Abstract class representing an annealer for simulated annealing optimization.
 * This class defines the structure for an annealer that can optimize a solution
 * @param <C> the type of the candidate solution
 */
public abstract class Annealer<C> {
    protected TemperatureRegulator temperatureRegulator;
    protected Acceptor acceptor;
    protected Evaluator<C> evaluator;
    protected NeighborGenerator<C> neighborGenerator;

    public abstract C optimize(C seed);
}
