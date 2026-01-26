package computation;

import computingMedia.Locus;
import computingMedia.media.Medium;
import computingMedia.sLoci.*;
import computingMedia.tLoci.*;

import java.util.HashMap;

/**
 * A field mapping loci to values of type T.
 *
 * @param <L> the type of locus
 * @param <T> the type of values stored in the field
 */
public abstract class Field<L extends Locus, T> extends HashMap<L, T> {
    protected Medium medium;

    public static abstract class Bool<L extends Locus> extends Field<L, Boolean> {
        protected abstract Bool<L> makeNew(Medium medium, boolean defaultValue);

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

    public static class BoolV extends Bool<Vertex> {
        public BoolV(Medium medium, boolean defaultValue) {
            this.medium = medium;
            for (Vertex v : medium) this.put(v, defaultValue);
        }
        public BoolV(Medium medium) {
            this(medium, false);
        }

        public BoolV makeNew(Medium medium, boolean defaultValue) {
            return new BoolV(medium, defaultValue);
        }

        public BoolV not() { return (BoolV) super.not(); }
        public BoolV or(BoolV other) { return (BoolV) super.or(other); }
        public BoolV and(BoolV other) { return (BoolV) super.and(other); }
        public BoolV xor(BoolV other) { return (BoolV) super.xor(other); }
    }
    public static class BoolVe extends Bool<Ve> {
        public BoolVe(Medium medium, boolean defaultValue) {
            this.medium = medium;
            for (Ve ve : medium.getVe()) this.put(ve, defaultValue);
        }
        public BoolVe(Medium medium) {
            this(medium, false);
        }

        public BoolVe makeNew(Medium medium, boolean defaultValue) {
            return new BoolVe(medium, defaultValue);
        }

        public BoolVe not() { return (BoolVe) super.not(); }
        public BoolVe or(BoolVe other) { return (BoolVe) super.or(other); }
        public BoolVe and(BoolVe other) { return (BoolVe) super.and(other); }
        public BoolVe xor(BoolVe other) { return (BoolVe) super.xor(other); }
    }
    public static class BoolVf extends Bool<Vf> {
        public BoolVf(Medium medium, boolean defaultValue) {
            this.medium = medium;
            for (Vf vf : medium.getVf()) this.put(vf, defaultValue);
        }
        public BoolVf(Medium medium) {
            this(medium, false);
        }

        public BoolVf makeNew(Medium medium, boolean defaultValue) {
            return new BoolVf(medium, defaultValue);
        }

        public BoolVf not() { return (BoolVf) super.not(); }
        public BoolVf or(BoolVf other) { return (BoolVf) super.or(other); }
        public BoolVf and(BoolVf other) { return (BoolVf) super.and(other); }
        public BoolVf xor(BoolVf other) { return (BoolVf) super.xor(other); }
    }

    public static class BoolE extends Bool<Edge> {
        public BoolE(Medium medium, boolean defaultValue) {
            this.medium = medium;
            for (Edge e : medium.getEdges()) this.put(e, defaultValue);
        }
        public BoolE(Medium medium) {
            this(medium, false);
        }

        public BoolE makeNew(Medium medium, boolean defaultValue) {
            return new BoolE(medium, defaultValue);
        }

        public BoolE not() { return (BoolE) super.not(); }
        public BoolE or(BoolE other) { return (BoolE) super.or(other); }
        public BoolE and(BoolE other) { return (BoolE) super.and(other); }
        public BoolE xor(BoolE other) { return (BoolE) super.xor(other); }
    }
    public static class BoolEv extends Bool<Ev> {
        public BoolEv (Medium medium, boolean defaultValue) {
            this.medium = medium;
            for (Ev ev : medium.getEv()) this.put(ev, defaultValue);
        }
        public BoolEv (Medium medium) {
            this(medium, false);
        }

        public BoolEv makeNew(Medium medium, boolean defaultValue) {
            return new BoolEv(medium, defaultValue);
        }

        public BoolEv not() { return (BoolEv) super.not(); }
        public BoolEv or(BoolEv other) { return (BoolEv) super.or(other); }
        public BoolEv and(BoolEv other) { return (BoolEv) super.and(other); }
        public BoolEv xor(BoolEv other) { return (BoolEv) super.xor(other); }
    }
    public static class BoolEf extends Bool<Ef> {
        public BoolEf(Medium medium, boolean defaultValue) {
            this.medium = medium;
            for (Ef ef : medium.getEf()) this.put(ef, defaultValue);
        }
        public BoolEf(Medium medium) {
            this(medium, false);
        }

        public BoolEf makeNew(Medium medium, boolean defaultValue) {
            return new BoolEf(medium, defaultValue);
        }

        public BoolEf not() { return (BoolEf) super.not(); }
        public BoolEf or(BoolEf other) { return (BoolEf) super.or(other); }
        public BoolEf and(BoolEf other) { return (BoolEf) super.and(other); }
        public BoolEf xor(BoolEf other) { return (BoolEf) super.xor(other); }
    }

    public static class BoolF extends Bool<Face> {
        public BoolF(Medium medium, boolean defaultValue) {
            this.medium = medium;
            for (Face f : medium.getFaces()) this.put(f, defaultValue);
        }
        public BoolF(Medium medium) {
            this(medium, false);
        }

        public Bool<Face> makeNew(Medium medium, boolean defaultValue) {
            return new BoolF(medium, defaultValue);
        }

        public BoolF not() { return (BoolF) super.not(); }
        public BoolF or(BoolF other) { return (BoolF) super.or(other); }
        public BoolF and(BoolF other) { return (BoolF) super.and(other); }
        public BoolF xor(BoolF other) { return (BoolF) super.xor(other); }
    }
    public static class BoolFv extends Bool<Fv> {
        public BoolFv (Medium medium, boolean defaultValue) {
            this.medium = medium;
            for (Fv fv : medium.getFv()) this.put(fv, defaultValue);
        }
        public BoolFv (Medium medium) {
            this(medium, false);
        }

        public BoolFv makeNew(Medium medium, boolean defaultValue) {
            return new BoolFv(medium, defaultValue);
        }

        public BoolFv not() { return (BoolFv) super.not(); }
        public BoolFv or(BoolFv other) { return (BoolFv) super.or(other); }
        public BoolFv and(BoolFv other) { return (BoolFv) super.and(other); }
        public BoolFv xor(BoolFv other) { return (BoolFv) super.xor(other); }
    }
    public static class BoolFe extends Bool<Fe> {
        public BoolFe(Medium medium, boolean defaultValue) {
            this.medium = medium;
            for (Fe fe : medium.getFe()) this.put(fe, defaultValue);
        }
        public BoolFe(Medium medium) {
            this(medium, false);
        }

        public BoolFe makeNew(Medium medium, boolean defaultValue) {
            return new BoolFe(medium, defaultValue);
        }

        public BoolFe not() { return (BoolFe) super.not(); }
        public BoolFe or(BoolFe other) { return (BoolFe) super.or(other); }
        public BoolFe and(BoolFe other) { return (BoolFe) super.and(other); }
        public BoolFe xor(BoolFe other) { return (BoolFe) super.xor(other); }
    }

    public static abstract class Int<L extends Locus> extends Field<L, Integer> {
        protected abstract Int<L> makeNew(Medium medium, int defaultValue);

        public Int<L> neg(){
            Int<L> result = makeNew(medium, 0);
            for (L locus : this.keySet()) result.put(locus, -this.get(locus));
            return result;
        }

        public Int<L> add(Int<L> other) {
            assert this.medium == other.medium :
                    "Fields must be defined on the same medium to perform addition.";
            Int<L> result = makeNew(medium, 0);
            for (L locus : this.keySet()) result.put(locus, this.get(locus) + other.get(locus));
            return result;
        }

        public Int<L> sub(Int<L> other) {
            assert this.medium == other.medium :
                    "Fields must be defined on the same medium to perform subtraction.";
            Int<L> result = makeNew(medium, 0);
            for (L locus : this.keySet()) result.put(locus, this.get(locus) - other.get(locus));
            return result;
        }

        public Int<L> mul(Int<L> other) {
            assert this.medium == other.medium :
                    "Fields must be defined on the same medium to perform multiplication.";
            Int<L> result = makeNew(medium, 0);
            for (L locus : this.keySet()) result.put(locus, this.get(locus) * other.get(locus));
            return result;
        }

        public Int<L> div(Int<L> other) {
            assert this.medium == other.medium :
                    "Fields must be defined on the same medium to perform division.";
            Int<L> result = makeNew(medium, 0);
            for (L locus : this.keySet()) {
                if (other.get(locus) == 0) throw new ArithmeticException("Division by zero in field division.");
                result.put(locus, this.get(locus) / other.get(locus));
            }
            return result;
        }

        public Int<L> mod(Int<L> other) {
            assert this.medium == other.medium :
                    "Fields must be defined on the same medium to perform modulus.";
            Int<L> result = makeNew(medium, 0);
            for (L locus : this.keySet()) result.put(locus, this.get(locus) % other.get(locus));
            return result;
        }

        public Int<L> min(Int<L> other) {
            assert this.medium == other.medium :
                    "Fields must be defined on the same medium to perform minimum.";
            Int<L> result = makeNew(medium, 0);
            for (L locus : this.keySet()) result.put(locus, Math.min(this.get(locus), other.get(locus)));
            return result;
        }

        public Int<L> max(Int<L> other) {
            assert this.medium == other.medium :
                    "Fields must be defined on the same medium to perform maximum.";
            Int<L> result = makeNew(medium, 0);
            for (L locus : this.keySet()) result.put(locus, Math.max(this.get(locus), other.get(locus)));
            return result;
        }
    }

    public static class IntV extends Int<Vertex> {
        public IntV(Medium medium, int defaultValue) {
            this.medium = medium;
            for (Vertex v : medium) this.put(v, defaultValue);
        }
        public IntV(Medium medium) {
            this(medium, 0);
        }

        public IntV makeNew(Medium medium, int defaultValue) {
            return new IntV(medium, defaultValue);
        }

        public IntV neg() { return (IntV) super.neg(); }
        public IntV add(IntV other) { return (IntV) super.add(other); }
        public IntV sub(IntV other) { return (IntV) super.sub(other); }
        public IntV mul(IntV other) { return (IntV) super.mul(other); }
        public IntV div(IntV other) { return (IntV) super.div(other); }
        public IntV mod(IntV other) { return (IntV) super.mod(other); }
        public IntV min(IntV other) { return (IntV) super.min(other); }
        public IntV max(IntV other) { return (IntV) super.max(other); }
    }
    public static class IntVe extends Int<Ve> {
        public IntVe(Medium medium, int defaultValue) {
            this.medium = medium;
            for (Ve ve : medium.getVe()) this.put(ve, defaultValue);
        }
        public IntVe(Medium medium) {
            this(medium, 0);
        }

        public IntVe makeNew(Medium medium, int defaultValue) {
            return new IntVe(medium, defaultValue);
        }

        public IntVe neg() { return (IntVe) super.neg(); }
        public IntVe add(IntVe other) { return (IntVe) super.add(other); }
        public IntVe sub(IntVe other) { return (IntVe) super.sub(other); }
        public IntVe mul(IntVe other) { return (IntVe) super.mul(other); }
        public IntVe div(IntVe other) { return (IntVe) super.div(other); }
        public IntVe mod(IntVe other) { return (IntVe) super.mod(other); }
        public IntVe min(IntVe other) { return (IntVe) super.min(other); }
        public IntVe max(IntVe other) { return (IntVe) super.max(other); }
    }
    public static class IntVf extends Int<Vf> {
        public IntVf(Medium medium, int defaultValue) {
            this.medium = medium;
            for (Vf vf : medium.getVf()) this.put(vf, defaultValue);
        }
        public IntVf(Medium medium) {
            this(medium, 0);
        }

        public IntVf makeNew(Medium medium, int defaultValue) {
            return new IntVf(medium, defaultValue);
        }

        public IntVf neg() { return (IntVf) super.neg(); }
        public IntVf add(IntVf other) { return (IntVf) super.add(other); }
        public IntVf sub(IntVf other) { return (IntVf) super.sub(other); }
        public IntVf mul(IntVf other) { return (IntVf) super.mul(other); }
        public IntVf div(IntVf other) { return (IntVf) super.div(other); }
        public IntVf mod(IntVf other) { return (IntVf) super.mod(other); }
        public IntVf min(IntVf other) { return (IntVf) super.min(other); }
        public IntVf max(IntVf other) { return (IntVf) super.max(other); }
    }

    public static class IntE extends Int<Edge> {
        public IntE(Medium medium, int defaultValue) {
            this.medium = medium;
            for (Edge e : medium.getEdges()) this.put(e, defaultValue);
        }
        public IntE(Medium medium) {
            this(medium, 0);
        }

        public IntE makeNew(Medium medium, int defaultValue) {
            return new IntE(medium, defaultValue);
        }

        public IntE neg() { return (IntE) super.neg(); }
        public IntE add(IntE other) { return (IntE) super.add(other); }
        public IntE sub(IntE other) { return (IntE) super.sub(other); }
        public IntE mul(IntE other) { return (IntE) super.mul(other); }
        public IntE div(IntE other) { return (IntE) super.div(other); }
        public IntE mod(IntE other) { return (IntE) super.mod(other); }
        public IntE min(IntE other) { return (IntE) super.min(other); }
        public IntE max(IntE other) { return (IntE) super.max(other); }
    }
    public static class IntEv extends Int<Ev> {
        public IntEv(Medium medium, int defaultValue) {
            this.medium = medium;
            for (Ev ev : medium.getEv()) this.put(ev, defaultValue);
        }
        public IntEv(Medium medium) {
            this(medium, 0);
        }

        public IntEv makeNew(Medium medium, int defaultValue) {
            return new IntEv(medium, defaultValue);
        }

        public IntEv neg() { return (IntEv) super.neg(); }
        public IntEv add(IntEv other) { return (IntEv) super.add(other); }
        public IntEv sub(IntEv other) { return (IntEv) super.sub(other); }
        public IntEv mul(IntEv other) { return (IntEv) super.mul(other); }
        public IntEv div(IntEv other) { return (IntEv) super.div(other); }
        public IntEv mod(IntEv other) { return (IntEv) super.mod(other); }
        public IntEv min(IntEv other) { return (IntEv) super.min(other); }
        public IntEv max(IntEv other) { return (IntEv) super.max(other); }
    }
    public static class IntEf extends Int<Ef> {
        public IntEf(Medium medium, int defaultValue) {
            this.medium = medium;
            for (Ef ef : medium.getEf()) this.put(ef, defaultValue);
        }
        public IntEf(Medium medium) {
            this(medium, 0);
        }

        public IntEf makeNew(Medium medium, int defaultValue) {
            return new IntEf(medium, defaultValue);
        }

        public IntEf neg() { return (IntEf) super.neg(); }
        public IntEf add(IntEf other) { return (IntEf) super.add(other); }
        public IntEf sub(IntEf other) { return (IntEf) super.sub(other); }
        public IntEf mul(IntEf other) { return (IntEf) super.mul(other); }
        public IntEf div(IntEf other) { return (IntEf) super.div(other); }
        public IntEf mod(IntEf other) { return (IntEf) super.mod(other); }
        public IntEf min(IntEf other) { return (IntEf) super.min(other); }
        public IntEf max(IntEf other) { return (IntEf) super.max(other); }
    }

    public static class IntF extends Int<Face> {
        public IntF(Medium medium, int defaultValue) {
            this.medium = medium;
            for (Face f : medium.getFaces()) this.put(f, defaultValue);
        }
        public IntF(Medium medium) {
            this(medium, 0);
        }

        public IntF makeNew(Medium medium, int defaultValue) {
            return new IntF(medium, defaultValue);
        }

        public IntF neg() { return (IntF) super.neg(); }
        public IntF add(IntF other) { return (IntF) super.add(other); }
        public IntF sub(IntF other) { return (IntF) super.sub(other); }
        public IntF mul(IntF other) { return (IntF) super.mul(other); }
        public IntF div(IntF other) { return (IntF) super.div(other); }
        public IntF mod(IntF other) { return (IntF) super.mod(other); }
        public IntF min(IntF other) { return (IntF) super.min(other); }
        public IntF max(IntF other) { return (IntF) super.max(other); }
    }
    public static class IntFv extends Int<Fv> {
        public IntFv(Medium medium, int defaultValue) {
            this.medium = medium;
            for (Fv fv : medium.getFv()) this.put(fv, defaultValue);
        }
        public IntFv(Medium medium) {
            this(medium, 0);
        }

        public IntFv makeNew(Medium medium, int defaultValue) {
            return new IntFv(medium, defaultValue);
        }

        public IntFv neg() { return (IntFv) super.neg(); }
        public IntFv add(IntFv other) { return (IntFv) super.add(other); }
        public IntFv sub(IntFv other) { return (IntFv) super.sub(other); }
        public IntFv mul(IntFv other) { return (IntFv) super.mul(other); }
        public IntFv div(IntFv other) { return (IntFv) super.div(other); }
        public IntFv mod(IntFv other) { return (IntFv) super.mod(other); }
        public IntFv min(IntFv other) { return (IntFv) super.min(other); }
        public IntFv max(IntFv other) { return (IntFv) super.max(other); }
    }
    public static class IntFe extends Int<Fe> {
        public IntFe(Medium medium, int defaultValue) {
            this.medium = medium;
            for (Fe fe : medium.getFe()) this.put(fe, defaultValue);
        }
        public IntFe(Medium medium) {
            this(medium, 0);
        }

        public IntFe makeNew(Medium medium, int defaultValue) {
            return new IntFe(medium, defaultValue);
        }

        public IntFe neg() { return (IntFe) super.neg(); }
        public IntFe add(IntFe other) { return (IntFe) super.add(other); }
        public IntFe sub(IntFe other) { return (IntFe) super.sub(other); }
        public IntFe mul(IntFe other) { return (IntFe) super.mul(other); }
        public IntFe div(IntFe other) { return (IntFe) super.div(other); }
        public IntFe mod(IntFe other) { return (IntFe) super.mod(other); }
        public IntFe min(IntFe other) { return (IntFe) super.min(other); }
        public IntFe max(IntFe other) { return (IntFe) super.max(other); }
    }
}
