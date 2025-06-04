package local.test;

import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.Edge;

public class MediumStatisticTest {
    private final Medium medium;

    MediumStatisticTest(Medium medium) {
        this.medium = medium;
    }

    public double[] gatherAngles(){
        double[] angles = new double[medium.getEdges().size()];
        int i = 0;
        for (Edge edge : medium.getEdges()) {
            double angle = edge.angleFromXAxis();
            if (angle < 0) angle += Math.PI; // segment direction does not matter
            angles[i++] = angle;
        }
        return angles;
    }

    public boolean testIsotropism(double alpha) {
        double[] angles = gatherAngles();

        UniformRealDistribution uniform = new UniformRealDistribution(0.0, Math.PI);
        KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
        return ksTest.kolmogorovSmirnovTest(uniform, angles, alpha);
    }
}
