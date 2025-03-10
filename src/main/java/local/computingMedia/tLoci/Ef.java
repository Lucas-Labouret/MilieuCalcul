package local.computingMedia.tLoci;

import local.computingMedia.sLoci.Edge;
import local.computingMedia.sLoci.Face;

public record Ef(Edge e, Face f) {
    public Ef {
        if (e == null || f == null) throw new IllegalArgumentException("Edge and face must not be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ef(Edge e1, Face f1))) return false;
        return e.equals(e1) && f.equals(f1);
    }

    @Override
    public int hashCode() {
        return e.hashCode() + f.hashCode();
    }

    @Override
    public String toString() {
        return "Ef{" + e + ", " + f + "}";
    }
}
