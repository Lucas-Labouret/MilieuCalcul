package computingMedia.tLoci;

import computingMedia.sLoci.Edge;
import computingMedia.sLoci.Face;

public class Fe extends TransferLocus {
    public final Face f;
    public final Edge e;

    public Fe(Face f, Edge e) {
        if (f == null || e == null) throw new IllegalArgumentException("Face and edge must not be null");
        this.f = f;
        this.e = e;
    }

    @Override public Ef getDual() { return new Ef(e, f); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fe oFe)) return false;
        return f.equals(oFe.f) && e.equals(oFe.e);
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
