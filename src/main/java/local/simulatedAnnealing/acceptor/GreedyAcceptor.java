package local.simulatedAnnealing.acceptor;

public class GreedyAcceptor implements Acceptor {
    @Override
    public boolean accept(double oldScore, double newScore, double temperature) {
        return newScore > oldScore;
    }
}
