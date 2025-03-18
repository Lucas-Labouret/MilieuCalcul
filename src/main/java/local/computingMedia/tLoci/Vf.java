package local.computingMedia.tLoci;

import local.computingMedia.sLoci.Face;
import local.computingMedia.sLoci.Vertex;

public record Vf(Vertex v, Face f) {
    public Vf {
        if (v == null || f == null) throw new IllegalArgumentException("Vertex and face must not be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vf(Vertex v1, Face f1))) return false;
        return v.equals(v1) && f.equals(f1);
    }

    @Override
    public int hashCode() {
        return v.hashCode() + f.hashCode();
    }

    @Override
    public String toString() {
        return "Vf{" + v + ", " + f + "}";
    }
}
