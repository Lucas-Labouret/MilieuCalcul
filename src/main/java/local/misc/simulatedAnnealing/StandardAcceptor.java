package local.misc.simulatedAnnealing;

public class StandardAcceptor implements Acceptor {
    @Override
    public boolean accept(double oldScore, double newScore, double heat) {
        double delta = oldScore - newScore;
        if (delta < 0) return true;
        return false; // Math.random() < Math.exp(-delta / heat);
    }
}
