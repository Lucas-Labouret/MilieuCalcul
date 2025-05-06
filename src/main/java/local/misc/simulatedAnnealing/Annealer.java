package local.misc.simulatedAnnealing;

public abstract class Annealer<C, E> {
    protected HeatRegulator heatRegulator;
    protected Acceptor acceptor;
    protected Evaluator<C, E> evaluator;
    protected RandomNeighborGenerator<C, E> randomNeighborGenerator;

    public abstract C optimize(C seed, E environment);
}
