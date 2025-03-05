package local.computingMedium;

import local.computingMedium.media.Medium;
import local.misc.geometry.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("serial")
public class Vertex extends Point {
    private final HashSet<Vertex> neighbors = new HashSet<>();
    String id = "";

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
        super(x+ Medium.randomEps(), y+ Medium.randomEps());
        this.isBorder = isBorder;
        this.isTopBorder = isTopBorder;
        this.isLeftBorder = isLeftBorder;
        this.isRightBorder = isRightBorder;
        this.isBottomBorder = isBottomBorder;
    }

    public Vertex(Vertex vertex) {
        this(
                vertex.getX(), vertex.getY(),
                vertex.isBorder(),
                vertex.isTopBorder(), vertex.isLeftBorder(), vertex.isRightBorder(), vertex.isBottomBorder()
        );
        this.id = vertex.id;
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
    public HashSet<Vertex> getNeighbors() { return neighbors; }

    public HashSet<Face> getSurroundingFaces() {
        HashSet<Face> t = new HashSet<>();
        getSurroundingFacesIn(t);
        return t;
    }

    public void getSurroundingFacesIn(Set<Face> faces) {
        ArrayList<Vertex> sortedNeighbors = new ArrayList<>(this.getNeighbors());
        sortedNeighbors.sort(new Medium.ClockWise(this));
        int nbNeighbours = sortedNeighbors.size();
        for (int i = 0; i < nbNeighbours; i++) {
            Vertex neighbor1 = sortedNeighbors.get(i % nbNeighbours);
            Vertex neighbor2 = sortedNeighbors.get((i + 1) % nbNeighbours);
            if (neighbor1.hasNeighbors(neighbor2)) {
                faces.add(new Face(this, neighbor1, neighbor2));
            }
        }
    }

    public void setId(String id) { this.id = id; }
    public String getId() { return id; }

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
//        String co = "("+ getX() + ',' + getY() + ')';
//        String identifiant = "Vertex " + id + " " + co+ " ";
//        StringBuilder sb = new StringBuilder(); // car concatenation dans une boucle
//        sb.append("Neighbors: ");
//        for (Vertex neighbor : neighbors) {
//            sb.append(neighbor.getId()).append(", ");
//        }
//        // enlever la virgule
//        if (!neighbors.isEmpty()) {
//            sb.delete(sb.length() - 2, sb.length());
//        }
//        return identifiant + sb.toString();

        //return "("+ String.valueOf(getX()).substring(2, 4) + ',' + String.valueOf(getY()).substring(2, 4) + ")";
        return id;
    }
}
