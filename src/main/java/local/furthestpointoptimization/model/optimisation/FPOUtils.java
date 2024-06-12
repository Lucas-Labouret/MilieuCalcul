package local.furthestpointoptimization.model.optimisation;

import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;

import java.util.HashSet;

public class FPOUtils {
    private FPOUtils(){}

    public static void buildFPO(VertexSet vertexSet, double convergenceTolerance){
        int nbIterations = 0;
        double oldFpo = 0;
        double newFpo = 0;
        do {
            double fpo = fpoIteration(vertexSet);
            oldFpo = newFpo;
            newFpo = fpo;
            nbIterations++;
            System.out.println("-------------------------------------");
            System.out.println("Iteration " + nbIterations + ", " + vertexSet.size() + " vertices left");
            System.out.println("Average min dist: " + vertexSet.getAverageMinDist() + ", max dist: " + vertexSet.getMaxDist());
            System.out.println("FPO: " + fpo + ", Progress: " + (newFpo - oldFpo));
        } while (/*newFpo < convergenceTolerance &&*/ newFpo - oldFpo > 1e-6);
    }

    public static double fpoIteration(VertexSet vertices){
        VertexSet vertexSet = new VertexSet(vertices.toArray(new Vertex[0]));
        for (Vertex vertex : vertexSet){
            if (vertex.isBorder()) continue;
            Vertex f = vertex;
            double rmax = vertices.getLocalMinDist(vertex);
            //System.out.println("Starting with " + vertex + " with rmax = " + rmax);
            delaunayRemove(vertices, vertex);
            HashSet<Triangle> triangles = vertices.getTriangles();
            for (Triangle triangle : triangles){
                //Vertex circumcenter = triangle.getOrthocenter();
                //double circumradius = triangle.getOrthocenterDist();
                Vertex circumcenter = triangle.getCircumcenter();
                double circumradius = triangle.getCircumRadius();
                //System.out.println("    Triangle: " + triangle + ", c = (" + circumcenter.getX() + ", " + circumcenter.getY() + "), r = " + circumradius);
                if (
                        circumcenter.getX() < 0 || circumcenter.getX() > vertices.getWidth() ||
                        circumcenter.getY() < 0 || circumcenter.getY() > vertices.getHeight() ||
                        vertices.contains(circumcenter) ||
                        !GeometricPrimitives.insidePolygon(circumcenter, vertices.getBorder())
                ) continue;
                if (circumradius > rmax){
                    rmax = circumradius;
                    f = circumcenter;
                }
            }
            //System.out.println("Found (" + f.getX() + ", " + f.getY() + ") with rmax = " + rmax + " /already contained: " + vertices.contains(f));
            vertex.setX(f.getX());
            vertex.setY(f.getY());
            delaunayInsert(vertices, vertex);
        }
        return vertices.getGlobalMinDist()/vertices.getMaxDist();
    }

    private static void delaunayRemove(VertexSet vertices, Vertex vertex){
        for (Vertex v : vertices) v.getNeighbors().clear();
        vertices.remove(vertex);
        vertices.delaunayTriangulate();
    }
    private static void delaunayInsert(VertexSet vertices, Vertex vertex){
        vertices.add(vertex);
        for (Vertex v : vertices) v.getNeighbors().clear();
        vertices.delaunayTriangulate();
    }
}