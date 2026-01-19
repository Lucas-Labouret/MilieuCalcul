package computation;

import computingMedia.Locus;
import computingMedia.media.Medium;
import computingMedia.sLoci.Edge;
import computingMedia.sLoci.Face;
import computingMedia.sLoci.SimplicialLocus;
import computingMedia.sLoci.Vertex;
import computingMedia.tLoci.TransferLocus;
import computingMedia.tLoci.Ve;

import java.util.HashMap;

/**
 * A field mapping loci to values of type T.
 *
 * @param <L> the type of locus
 * @param <T> the type of values stored in the field
 */
public abstract class Field<L extends Locus, T> extends HashMap<L, T> {
    protected Medium medium;

    public interface Simplicial{}

    public interface Transfer{}

    public static abstract class Bool<L extends Locus> extends Field<L, Boolean> {
        protected abstract Bool<L> makeNew(Medium medium, boolean defaultValue);

        public interface Simplicial {}
        public interface Transfer {}

        public Bool<L> not() {
            Bool<L> result = makeNew(medium, false);
            for (L locus : this.keySet()) result.put(locus, !this.get(locus));
            return result;
        }

        public Bool<L> or(Bool<L> other) {
            assert this.medium == other.medium :
                    "Fields must be defined on the same medium to perform logical operations.";
            Bool<L> result = makeNew(medium, false);
            for (L locus : this.keySet()) result.put(locus, this.get(locus) || other.get(locus));
            return result;
        }

        public Bool<L> and(Bool<L> other) {
            assert this.medium == other.medium :
                    "Fields must be defined on the same medium to perform logical operations.";
            Bool<L> result = makeNew(medium, false);
            for (L locus : this.keySet()) result.put(locus, this.get(locus) && other.get(locus));
            return result;
        }

        public Bool<L> xor(Bool<L> other) {
            assert this.medium == other.medium :
                    "Fields must be defined on the same medium to perform logical operations.";
            Bool<L> result = makeNew(medium, false);
            for (L locus : this.keySet()) result.put(locus, this.get(locus) ^ other.get(locus));
            return result;
        }
    }

    public static class BoolV extends Bool<Vertex> implements Simplicial {
        public BoolV(Medium medium, boolean defaultValue) {
            this.medium = medium;
            for (Vertex v : medium) this.put(v, defaultValue);
        }
        public BoolV(Medium medium) { this(medium, false); }

        public Bool<Vertex> makeNew(Medium medium, boolean defaultValue) {
            return new BoolV(medium, defaultValue);
        }

        public BoolV not() { return (BoolV) super.not(); }
        public BoolV or(BoolV other) { return (BoolV) super.or(other); }
        public BoolV and(BoolV other) { return (BoolV) super.and(other); }
        public BoolV xor(BoolV other) { return (BoolV) super.xor(other); }
    }

    public static class BoolVe extends Bool<Ve> implements Transfer {
        public BoolVe(Medium medium, boolean defaultValue) {
            this.medium = medium;
            for (Ve ve : medium.getVe()) this.put(ve, defaultValue);
        }
    }

    public static class BoolE extends Bool<Edge> implements Simplicial {
        public BoolE(Medium medium, boolean defaultValue) {
            this.medium = medium;
            for (Edge e : medium.getEdges()) this.put(e, defaultValue);
        }
        public BoolE(Medium medium) { this(medium, false); }

        public Bool<Edge> makeNew(Medium medium, boolean defaultValue) {
            return new BoolE(medium, defaultValue);
        }

        public BoolE not() { return (BoolE) super.not(); }
        public BoolE or(BoolE other) { return (BoolE) super.or(other); }
        public BoolE and(BoolE other) { return (BoolE) super.and(other); }
        public BoolE xor(BoolE other) { return (BoolE) super.xor(other); }
    }

    public static class BoolF extends Bool<Face> implements Simplicial {
        public BoolF(Medium medium, boolean defaultValue) {
            this.medium = medium;
            for (Face f : medium.getFaces()) this.put(f, defaultValue);
        }
        public BoolF(Medium medium) { this(medium, false); }

        public Bool<Face> makeNew(Medium medium, boolean defaultValue) {
            return new BoolF(medium, defaultValue);
        }

        public BoolF not() { return (BoolF) super.not(); }
        public BoolF or(BoolF other) { return (BoolF) super.or(other); }
        public BoolF and(BoolF other) { return (BoolF) super.and(other); }
        public BoolF xor(BoolF other) { return (BoolF) super.xor(other); }
    }
}

