package local.computingMedia.tLoci;

import local.computingMedia.sLoci.Edge;
import local.computingMedia.sLoci.Face;

public record Fe(Face f, Edge e) {
    public Fe {
        if (f == null || e == null) throw new IllegalArgumentException("Face and edge must not be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fe(Face f1, Edge e1))) return false;
        return f.equals(f1) && e.equals(e1);
    }

    @Override
    public int hashCode() {
        return f.hashCode() + e.hashCode();
    }

    @Override
    public String toString() {
        return "Fe{" + f + ", " + e + "}";
    }
}
