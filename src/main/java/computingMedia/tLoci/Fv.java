package computingMedia.tLoci;

import computingMedia.sLoci.Face;
import computingMedia.sLoci.Vertex;

public class Fv extends TransferLocus {
    public final Face f;
    public final Vertex v;

    public Fv(Face f, Vertex v) {
        if (f == null || v == null) throw new IllegalArgumentException("Face and vertex must not be null");
        this.f = f;
        this.v = v;
    }

    @Override public Vf getDual() { return new Vf(v, f); }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Fv oFv)) return false;
        return f.equals(oFv.f) && v.equals(oFv.v);
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

