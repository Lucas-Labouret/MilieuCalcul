package local.simulatedAnnealing.acceptor;

@FunctionalInterface
public interface Acceptor {
    /**
     * Determines whether to accept a new solution based on the old score, new score, and current temperature (heat).
     *
     * @param oldScore the score of the current solution
     * @param newScore the score of the new solution
     * @param temperature the current temperature (heat) in the simulated annealing process
     * @return true if the new solution should be accepted, false otherwise
     */
    boolean accept(double oldScore, double newScore, double temperature);
}
