package computingMedia.tLoci;

import computingMedia.sLoci.Edge;
import computingMedia.sLoci.Face;

public class Ef extends TransferLocus {
    public final Edge e;
    public final Face f;

    public Ef(Edge e, Face f) {
        if (e == null || f == null) throw new IllegalArgumentException("Edge and face must not be null");
        this.e = e;
        this.f = f;
    }

    @Override public Fe getDual() { return new Fe(f, e); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ef oEf)) return false;
        return e.equals(oEf.e) && f.equals(oEf.f);
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
