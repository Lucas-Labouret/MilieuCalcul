package computingMedia.tLoci;

import computingMedia.sLoci.Edge;
import computingMedia.sLoci.Vertex;

public class Ve extends TransferLocus {
    public final Vertex v;
    public final Edge e;

    public Ve(Vertex v, Edge e) {
        if (v == null || e == null) throw new IllegalArgumentException("Vertex and edge must not be null");
        this.v = v;
        this.e = e;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Ve oVe)) return false;
        return v.equals(oVe.v) && e.equals(oVe.e);
    }

    @Override
    public int hashCode() {
        return v.hashCode() + e.hashCode();
    }

    @Override
    public String toString() {
        return "Ve{" + v + ", " + e + "}";
    }
}
