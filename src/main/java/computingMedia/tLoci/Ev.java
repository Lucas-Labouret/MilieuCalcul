package computingMedia.tLoci;

import computingMedia.sLoci.Edge;
import computingMedia.sLoci.Vertex;

public class Ev extends TransferLocus {
    public final Edge e;
    public final Vertex v;

    public Ev(Edge e, Vertex v) {
        if (e == null || v == null) throw new IllegalArgumentException("Edge and vertex must not be null");
        this.e = e;
        this.v = v;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Ev oEv)) return false;
        return e.equals(oEv.e) && v.equals(oEv.v);
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
