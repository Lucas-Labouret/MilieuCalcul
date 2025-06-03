package local.test;

import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.Edge;

import java.util.Arrays;

public class IsotropismStatisticTester {
    private final Medium medium;

    IsotropismStatisticTester(Medium medium) {
        this.medium = medium;
    }

    private double[] gatherAnglesSorted(){
        double[] angles = new double[medium.getEdges().size()];
        int i = 0;
        for (Edge edge : medium.getEdges()) {
            double angle = edge.angleFromXAxis();
            if (angle < 0) angle += 2 * Math.PI; // Normalize angle to [0, 2π[
            if (angle > Math.PI) angle -= Math.PI; // Reduce to [0, π]
            angles[i++] = angle;
        }
        Arrays.sort(angles);
        return angles;
    }

    public double kolmogorovSmirnovStatistic(double[] angles) {
        double d = 0;
        for (int i = 0; i < angles.length; i++) {
            double cumulativeProbability = angles[i]/ Math.PI;
            double newD = Math.abs(cumulativeProbability - i / (double) angles.length);
            if (newD > d) {
                d = newD;
            }
        }
        return d;
    }

}
