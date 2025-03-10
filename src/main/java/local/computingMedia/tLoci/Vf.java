package local.computingMedia.tLoci;

import local.computingMedia.sLoci.Edge;
import local.computingMedia.sLoci.Vertex;

public record Vf(Vertex v, Edge e) {
    public Vf {
        if (v == null || e == null) throw new IllegalArgumentException("Vertex and edge must not be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vf(Vertex v1, Edge e1))) return false;
        return v.equals(v1) && e.equals(e1);
    }

    @Override
    public int hashCode() {
        return v.hashCode() + e.hashCode();
    }

    @Override
    public String toString() {
        return "Vf{" + v + ", " + e + "}";
    }
}
