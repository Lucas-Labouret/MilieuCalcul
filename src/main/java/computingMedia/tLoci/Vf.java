package computingMedia.tLoci;

import computingMedia.sLoci.Face;
import computingMedia.sLoci.Vertex;

public class Vf extends TransferLocus {
    public final Vertex v;
    public final Face f;

    public Vf(Vertex v, Face f) {
        if (v == null || f == null) throw new IllegalArgumentException("Vertex and face must not be null");
        this.v = v;
        this.f = f;
    }

    @Override public Fv getDual() { return new Fv(f, v); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vf oVf)) return false;
        return v.equals(oVf.v) && f.equals(oVf.f);
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
