package local.misc.simulatedAnnealing;

@FunctionalInterface
public interface Acceptor {
    boolean accept(double oldScore, double newScore, double heat);
}
