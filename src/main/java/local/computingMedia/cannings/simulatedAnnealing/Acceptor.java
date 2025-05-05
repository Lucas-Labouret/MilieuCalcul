package local.computingMedia.cannings.simulatedAnnealing;

@FunctionalInterface
public interface Acceptor {
    boolean accept(double oldScore, double newScore, double heat);
}
