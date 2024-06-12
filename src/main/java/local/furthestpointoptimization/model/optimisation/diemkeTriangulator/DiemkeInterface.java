package local.furthestpointoptimization.model.optimisation.diemkeTriangulator;

import local.furthestpointoptimization.model.optimisation.Triangle;
import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;

import java.util.ArrayList;
import java.util.HashMap;

public class DiemkeInterface {
    public static void triangulate(VertexSet vertexSet) throws NotEnoughPointsException {
        HashMap<Vector2D, Vertex> vectorToVertex = new HashMap<>();
        ArrayList<Vector2D> pointSet = new ArrayList<>();

        for (Vertex vertex : vertexSet){
            vertex.getNeighbors().clear();

            Vector2D vector = new Vector2D(vertex.getX(), vertex.getY());
            vectorToVertex.put(vector, vertex);
            pointSet.add(vector);
        }

        DelaunayTriangulator triangulator = new DelaunayTriangulator(pointSet);
        triangulator.triangulate();

        for (Triangle2D triangle : triangulator.getTriangles()){
            Vertex a = vectorToVertex.get(triangle.a);
            Vertex b = vectorToVertex.get(triangle.b);
            Vertex c = vectorToVertex.get(triangle.c);
            a.addNeighbor(b);
            b.addNeighbor(c);
            c.addNeighbor(a);

            vertexSet.triangles.add(new Triangle(a, b, c));
        }
    }
}
