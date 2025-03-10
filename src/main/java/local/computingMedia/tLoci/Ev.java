package local.computingMedia.tLoci;

import local.computingMedia.sLoci.Edge;
import local.computingMedia.sLoci.Vertex;

public record Ev(Edge e, Vertex v) {
    public Ev {
        if (e == null || v == null) throw new IllegalArgumentException("Edge and vertex must not be null");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Ev(Edge e1, Vertex v1))) return false;
        return e.equals(e1) && v.equals(v1);
    }

    @Override
    public int hashCode() {
        return e.hashCode() + v.hashCode();
    }

    @Override
    public String toString() {
        return "Ev{" + e + ", " + v + "}";
    }
}
