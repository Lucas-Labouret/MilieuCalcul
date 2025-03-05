package local.optimization;

import local.computingMedia.Face;
import local.computingMedia.Vertex;
import local.computingMedia.media.Medium;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;

public class FPO {
    private FPO(){}

    public static void buildFPO(Medium medium, double convergenceTolerance){
        int nbIterations = 0;
        double oldFpo;
        double newFpo = 0;

        Instant start = Instant.now();
        do {
            double fpo = fpoIteration(medium);
            oldFpo = newFpo;
            newFpo = fpo;
            nbIterations++;
            System.out.println("-------------------------------------");
            System.out.println("Iteration " + nbIterations + ", " + medium.size() + " vertices left");
            System.out.println("Average min dist: " + medium.getAverageMinDist() + ", max dist: " + medium.getMaxNeighborhoodMaxDist());
            System.out.println("FPO: " + fpo + ", Progress: " + (newFpo - oldFpo));
        } while (newFpo < convergenceTolerance && newFpo - oldFpo > 1e-6);
        Instant end = Instant.now();
        System.out.println("FPO done in " + Duration.between(start, end).toMinutes() + "min");
    }

    public static double fpoIteration(Medium vertices){
        Medium medium = vertices.copy();
        for (Vertex vertex : medium){
            if (vertex.isBorder()) continue;
            Vertex f = vertex;
            double rmax = vertices.getLocalMinDist(vertex);
            delaunayRemove(vertices, vertex);
            HashSet<Face> faces = vertices.getFaces();
            for (Face face : faces){
                Vertex circumcenter = face.getCircumcenter();
                double circumradius = face.getCircumRadius();
                if (!vertices.isInBorder(circumcenter) || vertices.contains(circumcenter)) continue;
                if (circumradius > rmax){
                    rmax = circumradius;
                    f = circumcenter;
                }
            }
            vertex.setX(f.getX());
            vertex.setY(f.getY());
            delaunayInsert(vertices, vertex);
        }
        return vertices.getAverageMinDist()/vertices.getMaxNeighborhoodMaxDist();
    }

    private static void delaunayRemove(Medium vertices, Vertex vertex){
        for (Vertex v : vertices) v.getNeighbors().clear();
        vertices.remove(vertex);
        vertices.delaunayTriangulate();
    }
    private static void delaunayInsert(Medium vertices, Vertex vertex){
        vertices.add(vertex);
        for (Vertex v : vertices) v.getNeighbors().clear();
        vertices.delaunayTriangulate();
    }
}
