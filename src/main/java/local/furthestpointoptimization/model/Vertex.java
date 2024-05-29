package local.furthestpointoptimization.model;

import java.util.HashSet;

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

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }

    @Override
    public String toString() {
        String co = "("+ getX() + ',' + getY() + ')';
        String identifiant = "Vertex " + id + " " + co;
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
