package local.computingMedia.tLoci;

import local.computingMedia.sLoci.Edge;
import local.computingMedia.sLoci.Vertex;

public record Ve(Vertex v, Edge e) {
    public Ve {
        if (v == null || e == null) throw new IllegalArgumentException("Vertex and edge must not be null");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Ve(Vertex v1, Edge e1))) return false;
        return v.equals(v1) && e.equals(e1);
    }

    @Override
    public int hashCode() {
        return v.hashCode() + e.hashCode();
    }

    @Override
    public String toString() {
        return "Ve{" + v + ", " + e + "}";
    }
}
