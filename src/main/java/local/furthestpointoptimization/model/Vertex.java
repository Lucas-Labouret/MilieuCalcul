package local.furthestpointoptimization.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Vertex extends Point {
    private final VertexSet neighbors = new VertexSet();
    int id;

    public Vertex(double x, double y) {
        super(x, y);
    }

    public boolean hasNeighbors(Vertex v) { return neighbors.contains(v); }
    public void addNeighbor(Vertex v) {
        if (neighbors.add(v)) v.addNeighbor(this);
    }
    public void removeNeighbor(Vertex v) {
        if (neighbors.remove(v)) v.removeNeighbor(this);
    }

    /**returns a reference*/
    public VertexSet getNeighbors() {
        return neighbors;
    }

    public HashSet<Triangle> getSurroundTriangle() {
        HashSet<Triangle> t = new HashSet<>();
        getSurroundTriangleIn(t);
        return t;
    }

    public void getSurroundTriangleIn(Set<Triangle> triangleSet) {
        ArrayList<Vertex> sortedNeighbors = new ArrayList<>(this.getNeighbors());
        sortedNeighbors.sort(new VertexSet.ClockWise(this));
        int nbNeighbours = sortedNeighbors.size();
        for (int i = 0; i < nbNeighbours; i++) {
            Vertex neighbor1 = sortedNeighbors.get(i % nbNeighbours);
            Vertex neighbor2 = sortedNeighbors.get((i + 1) % nbNeighbours);
            if (neighbor1.hasNeighbors(neighbor2)) {
                triangleSet.add(new Triangle(this, neighbor1, neighbor2));
            }
        }
    }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }

    public static void link(Vertex v1, Vertex v2) {
        v1.neighbors.add(v2);
        v2.neighbors.add(v1);
    }

    public static void unlink(Vertex v1, Vertex v2) {
        v1.neighbors.remove(v2);
        v2.neighbors.remove(v1);
    }

    @Override
    public String toString() {
        String co = "("+ getX() + ',' + getY() + ')';
        String identifiant = "Vertex " + id + " " + co+ " ";
        StringBuilder sb = new StringBuilder(); // car concatenation dans une boucle
        sb.append("Neighbors: ");
        for (Vertex neighbor : neighbors) {
            sb.append(neighbor.getId()).append(", ");
        }
        // enlever la virgule
        if (!neighbors.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return identifiant + sb.toString();
    }
}
