package local.furthestpointoptimization.model;

import local.furthestpointoptimization.model.optimisation.Triangle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Vertex extends Point {
    private final VertexSet neighbors = new VertexSet();
    int id;

    private final boolean isBorder;
    private final boolean isTopBorder;
    private final boolean isLeftBorder;
    private final boolean isRightBorder;
    private final boolean isBottomBorder;


    public Vertex(double x, double y) {
        this(x, y, false, false, false, false, false);
    }

    public Vertex(double x, double y, boolean isBorder) {
        this(x, y, isBorder, false, false, false, false);
    }

    public Vertex(
            double x, double y,
            boolean isBorder,
            boolean isTopBorder, boolean isLeftBorder, boolean isRightBorder, boolean isBottomBorder
    ) {
        super(x, y);
        this.isBorder = isBorder;
        this.isTopBorder = isTopBorder;
        this.isLeftBorder = isLeftBorder;
        this.isRightBorder = isRightBorder;
        this.isBottomBorder = isBottomBorder;
    }

    public boolean isBorder() { return isBorder; }
    public boolean isTopBorder() { return isTopBorder; }
    public boolean isLeftBorder() { return isLeftBorder; }
    public boolean isRightBorder() { return isRightBorder; }
    public boolean isBottomBorder() { return isBottomBorder; }

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

    public Optional<Vertex> getKNeighbor(int k) {
        ArrayList<Vertex> vs = new ArrayList<>();
        vs.addAll(getNeighbors());

        Vertex maxX = vs.getFirst();
        for (Vertex v : vs) {
            if (v.getX() > maxX.getX()) {
                maxX=v;
            }
        }
        vs.sort(new Point.CompareByAngleDistance(this, maxX, false));
        try {
            return Optional.of(vs.get(k));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
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
