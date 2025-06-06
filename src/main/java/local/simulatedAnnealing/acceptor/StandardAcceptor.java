package local.simulatedAnnealing.acceptor;

/**
 * Standard implementation of the Acceptor interface for simulated annealing.
 */
public class StandardAcceptor implements Acceptor {
    @Override
    public boolean accept(double oldScore, double newScore, double temperature) {
        double delta = oldScore - newScore;
        if (delta < 0) return true;
        return Math.random() < Math.exp(-delta / temperature);
    }
}
