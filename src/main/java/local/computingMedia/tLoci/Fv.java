package local.computingMedia.tLoci;

import local.computingMedia.sLoci.Face;
import local.computingMedia.sLoci.Vertex;

public record Fv(Face f, Vertex v) {
    public Fv {
        if (f == null || v == null) throw new IllegalArgumentException("Face and vertex must not be null");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Fv(Face f1, Vertex v1))) return false;
        return f.equals(f1) && v.equals(v1);
    }

    @Override
    public int hashCode() {
        return f.hashCode() + v.hashCode();
    }

    @Override
    public String toString() {
        return "Fv{" + f + ", " + v + "}";
    }
}

