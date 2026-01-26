package computation;

import computingMedia.media.HardRectangleMedium;
import computingMedia.media.Medium;
import computingMedia.sLoci.*;
import computingMedia.tLoci.*;

public class Reduction {
    Medium medium;
    public Reduction(Medium medium) {
        this.medium = medium;
    }

    @FunctionalInterface
    public interface Def {
        Field<? extends SimplicialLocus, ?> reduce(Field<? extends TransferLocus, ?> field);
    }

    public Field.BoolV orVe(Field.BoolVe boolVe) {
        Field.BoolV result = new Field.BoolV(medium, false);
        for (Vertex v : medium) for (Ve ve : v.getVes())
            result.put(v, result.get(v) || boolVe.get(ve));
        return result;
    }
    public Field.BoolV orVf(Field.BoolVf boolVf) {
        Field.BoolV result = new Field.BoolV(medium, false);
        for (Vertex v : medium) for (Vf vf : v.getVfs())
            result.put(v, result.get(v) || boolVf.get(vf));
        return result;
    }
    public Field.BoolV andVe(Field.BoolVe boolVe) {
        Field.BoolV result = new Field.BoolV(medium, true);
        for (Vertex v : medium) for (Ve ve : v.getVes())
            result.put(v, result.get(v) && boolVe.get(ve));
        return result;
    }
    public Field.BoolV andVf(Field.BoolVf boolVf) {
        Field.BoolV result = new Field.BoolV(medium, true);
        for (Vertex v : medium) for (Vf vf : v.getVfs())
            result.put(v, result.get(v) && boolVf.get(vf));
        return result;
    }
    public Field.BoolV xorVe(Field.BoolVe boolVe) {
        Field.BoolV result = new Field.BoolV(medium, false);
        for (Vertex v : medium) for (Ve ve : v.getVes())
            result.put(v, result.get(v) ^ boolVe.get(ve));
        return result;
    }
    public Field.BoolV xorVf(Field.BoolVf boolVf) {
        Field.BoolV result = new Field.BoolV(medium, false);
        for (Vertex v : medium) for (Vf vf : v.getVfs())
            result.put(v, result.get(v) ^ boolVf.get(vf));
        return result;
    }

    public Field.BoolE orEv(Field.BoolEv boolEv) {
        Field.BoolE result = new Field.BoolE(medium, false);
        for (Edge e : medium.getEdges()) for (Ev ev : e.getEvs())
            result.put(e, result.get(e) || boolEv.get(ev));
        return result;
    }
    public Field.BoolE orEf(Field.BoolEf boolEf) {
        Field.BoolE result = new Field.BoolE(medium, false);
        for (Edge e : medium.getEdges()) for (Ef ef : e.getEfs())
            result.put(e, result.get(e) || boolEf.get(ef));
        return result;
    }
    public Field.BoolE andEv(Field.BoolEv boolEv) {
        Field.BoolE result = new Field.BoolE(medium, true);
        for (Edge e : medium.getEdges()) for (Ev ev : e.getEvs())
            result.put(e, result.get(e) && boolEv.get(ev));
        return result;
    }
    public Field.BoolE andEf(Field.BoolEf boolEf) {
        Field.BoolE result = new Field.BoolE(medium, true);
        for (Edge e : medium.getEdges()) for (Ef ef : e.getEfs())
            result.put(e, result.get(e) && boolEf.get(ef));
        return result;
    }
    public Field.BoolE xorEv(Field.BoolEv boolEv) {
        Field.BoolE result = new Field.BoolE(medium, false);
        for (Edge e : medium.getEdges()) for (Ev ev : e.getEvs())
            result.put(e, result.get(e) ^ boolEv.get(ev));
        return result;
    }
    public Field.BoolE xorEf(Field.BoolEf boolEf) {
        Field.BoolE result = new Field.BoolE(medium, false);
        for (Edge e : medium.getEdges()) for (Ef ef : e.getEfs())
            result.put(e, result.get(e) ^ boolEf.get(ef));
        return result;
    }

    public Field.BoolF orFv(Field.BoolFv boolFv) {
        Field.BoolF result = new Field.BoolF(medium, false);
        for (Face f : medium.getFaces()) for (Fv fv : f.getFvs())
            result.put(f, result.get(f) || boolFv.get(fv));
        return result;
    }
    public Field.BoolF orFe(Field.BoolFe boolFe) {
        Field.BoolF result = new Field.BoolF(medium, false);
        for (Face f : medium.getFaces()) for (Fe fe : f.getFes())
            result.put(f, result.get(f) || boolFe.get(fe));
        return result;
    }
    public Field.BoolF andFv(Field.BoolFv boolFv) {
        Field.BoolF result = new Field.BoolF(medium, true);
        for (Face f : medium.getFaces()) for (Fv fv : f.getFvs())
            result.put(f, result.get(f) && boolFv.get(fv));
        return result;
    }
    public Field.BoolF andFe(Field.BoolFe boolFe) {
        Field.BoolF result = new Field.BoolF(medium, true);
        for (Face f : medium.getFaces()) for (Fe fe : f.getFes())
            result.put(f, result.get(f) && boolFe.get(fe));
        return result;
    }
    public Field.BoolF xorFv(Field.BoolFv boolFv) {
        Field.BoolF result = new Field.BoolF(medium, false);
        for (Face f : medium.getFaces()) for (Fv fv : f.getFvs())
            result.put(f, result.get(f) ^ boolFv.get(fv));
        return result;
    }
    public Field.BoolF xorFe(Field.BoolFe boolFe) {
        Field.BoolF result = new Field.BoolF(medium, false);
        for (Face f : medium.getFaces()) for (Fe fe : f.getFes())
            result.put(f, result.get(f) ^ boolFe.get(fe));
        return result;
    }

    public Field.IntV addVe(Field.IntVe intVe) {
        Field.IntV result = new Field.IntV(medium, 0);
        for (Vertex v : medium) for (Ve ve : v.getVes())
            result.put(v, result.get(v) + intVe.get(ve));
        return result;
    }
    public Field.IntV addVf(Field.IntVf intVf) {
        Field.IntV result = new Field.IntV(medium, 0);
        for (Vertex v : medium) for (Vf vf : v.getVfs())
                result.put(v, result.get(v) + intVf.get(vf));
        return result;
    }
    public Field.IntV mulVe(Field.IntVe intVe) {
        Field.IntV result = new Field.IntV(medium, 1);
        for (Vertex v : medium) for (Ve ve : v.getVes())
            result.put(v, result.get(v) * intVe.get(ve));
        return result;
    }
    public Field.IntV mulVf(Field.IntVf intVf) {
        Field.IntV result = new Field.IntV(medium, 1);
        for (Vertex v : medium) for (Vf vf : v.getVfs())
                result.put(v, result.get(v) * intVf.get(vf));
        return result;
    }
    public Field.IntV minVe(Field.IntVe intVe) {
        Field.IntV result = new Field.IntV(medium, Integer.MAX_VALUE);
        for (Vertex v : medium) for (Ve ve : v.getVes())
            result.put(v, Math.min(result.get(v), intVe.get(ve)));
        return result;
    }
    public Field.IntV minVf(Field.IntVf intVf) {
        Field.IntV result = new Field.IntV(medium, Integer.MAX_VALUE);
        for (Vertex v : medium) for (Vf vf : v.getVfs())
                result.put(v, Math.min(result.get(v), intVf.get(vf)));
        return result;
    }
    public Field.IntV maxVe(Field.IntVe intVe) {
        Field.IntV result = new Field.IntV(medium, Integer.MIN_VALUE);
        for (Vertex v : medium) for (Ve ve : v.getVes())
            result.put(v, Math.max(result.get(v), intVe.get(ve)));
        return result;
    }
    public Field.IntV maxVf(Field.IntVf intVf) {
        Field.IntV result = new Field.IntV(medium, Integer.MIN_VALUE);
        for (Vertex v : medium) for (Vf vf : v.getVfs())
                result.put(v, Math.max(result.get(v), intVf.get(vf)));
        return result;
    }
}
