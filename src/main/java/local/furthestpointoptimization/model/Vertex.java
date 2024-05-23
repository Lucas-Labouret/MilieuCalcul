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
    public VertexSet getNeighbors() {
        return neighbors;
    }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }

    public String toString() {
        return String.valueOf(id);
    }
}
