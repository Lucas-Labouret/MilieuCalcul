package local.computingMedia.optimization;

import local.computingMedia.sLoci.Face;
import local.computingMedia.sLoci.Vertex;
import local.computingMedia.media.Medium;

import java.time.Duration;
import java.time.Instant;

/**
 * FPO (Farthest Point Optimization) algorithm for optimizing the placement of vertices in a medium.
 * This algorithm iteratively moves vertices to improve the distribution of points.
 * The goal is to maximize the minimum distance between vertices.<br>
 * See <a href="https://doi.org/10.1145/2018323.2018345">here</a> for more details.
 */
public class FPO {
    private FPO(){}

    /**
     * Execute the FPO algorithm on the given medium.
     * @param medium The medium to optimize.
     * @param convergenceTolerance The tolerance for convergence. The algorithm stops when the FPO score converges or the progress is negligible.
     */
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
            System.out.println("Iteration " + nbIterations);
            System.out.println("Average min dist: " + medium.getAverageMinDist() + ", max dist: " + medium.getMaxNeighborhoodMaxDist());
            System.out.println("FPO: " + fpo + ", Progress: " + (newFpo - oldFpo));
        } while (newFpo < convergenceTolerance && newFpo - oldFpo > 1e-6);
        Instant end = Instant.now();
        System.out.println("FPO done in " + Duration.between(start, end).toMinutes() + "min");
    }

    /**
     * Perform one iteration of the FPO algorithm.
     * @param medium The medium to optimize.
     * @return The FPO score after the iteration.
     */
    public static double fpoIteration(Medium medium){
        Medium tmpMedium = medium.copy(); // Create a copy of the medium to avoid modifying it during the iteration

        // Iterate over all vertices in the medium to find a better position for each vertex
        for (Vertex vertex : tmpMedium){
            if (vertex.isBorder()) continue; // Skip border vertices that cannot be moved
            Vertex f = vertex;
            double rmax = medium.getLocalMinDist(vertex);
            delaunayRemove(medium, vertex); //Remove the current vertex from the medium to find its new position

            // Find the circumcenter of the face with the largest circumradius
            // i.e. the point that farthest away from any other vertex
            for (Face face : medium.getFaces()){
                Vertex circumcenter = face.getCircumcenter();
                double circumradius = face.getCircumradius();

                // We don't want to move the vertex out of the medium or directly onto another vertex
                if (!medium.isInBorder(circumcenter) || medium.contains(circumcenter)) continue;

                if (circumradius > rmax){
                    rmax = circumradius;
                    f = circumcenter;
                }
            }

            // Move the vertex to its new position
            vertex.setX(f.getX());
            vertex.setY(f.getY());
            delaunayInsert(medium, vertex);
        }

        // Calculate the FPO score based on the average minimum distance and the maximum neighborhood distance
        // This is a measure of how well the vertices are distributed, and converges to 1 in the ideal case
        return medium.getAverageMinDist()/medium.getMaxNeighborhoodMaxDist();
    }

    /** * Remove a vertex from the medium while maintaining the Delaunay triangulation. */
    private static void delaunayRemove(Medium vertices, Vertex vertex){
        //TODO: Implement a more efficient removal method
        for (Vertex v : vertices) v.getNeighbors().clear();
        vertices.remove(vertex);
        vertices.delaunayTriangulate();
    }

    /** Insert a vertex into the medium while maintaining the Delaunay triangulation. */
    private static void delaunayInsert(Medium vertices, Vertex vertex){
        //TODO: Implement a more efficient insertion method
        vertices.add(vertex);
        for (Vertex v : vertices) v.getNeighbors().clear();
        vertices.delaunayTriangulate();
    }
}
