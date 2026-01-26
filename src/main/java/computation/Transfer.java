package computation;

import computingMedia.media.Medium;
import computingMedia.tLoci.*;

public class Transfer {
    Medium medium;
    public Transfer(Medium medium) {
        this.medium = medium;
    }

    @FunctionalInterface
    public interface Def {
        Field<? extends TransferLocus, ?> transfer(Field<? extends TransferLocus, ?> field);
    }

    public Field.BoolEv boolVeTransfer(Field.BoolVe boolVe) {
        Field.BoolEv result = new Field.BoolEv(medium);
        for (Ve ve : boolVe.keySet())
            result.put(ve.getDual(), boolVe.get(ve));
        return result;
    }

    public Field.BoolVe boolEvTransfer(Field.BoolEv boolEv) {
        Field.BoolVe result = new Field.BoolVe(medium);
        for (Ev ev : boolEv.keySet())
            result.put(ev.getDual(), boolEv.get(ev));
        return result;
    }

    public Field.BoolFv boolVfTransfer(Field.BoolVf boolVf) {
        Field.BoolFv result = new Field.BoolFv(medium);
        for (Vf vf : boolVf.keySet())
            result.put(vf.getDual(), boolVf.get(vf));
        return result;
    }

    public Field.BoolVf boolFvTransfer(Field.BoolFv boolFv) {
        Field.BoolVf result = new Field.BoolVf(medium);
        for (Fv fv : boolFv.keySet())
            result.put(fv.getDual(), boolFv.get(fv));
        return result;
    }

    public Field.BoolEf boolFeTransfer(Field.BoolFe boolFe) {
        Field.BoolEf result = new Field.BoolEf(medium);
        for (Fe fe : boolFe.keySet())
            result.put(fe.getDual(), boolFe.get(fe));
        return result;
    }

    public Field.BoolFe boolEfTransfer(Field.BoolEf boolEf) {
        Field.BoolFe result = new Field.BoolFe(medium);
        for (Ef ef : boolEf.keySet())
            result.put(ef.getDual(), boolEf.get(ef));
        return result;
    }

    public Field.IntEv intVeTransfer(Field.IntVe intVe){
        Field.IntEv result = new Field.IntEv(medium);
        for (Ve ve : intVe.keySet())
            result.put(ve.getDual(), intVe.get(ve));
        return result;
    }

    public Field.IntVe intEvTransfer(Field.IntEv intEv){
        Field.IntVe result = new Field.IntVe(medium);
        for (Ev ev : intEv.keySet())
            result.put(ev.getDual(), intEv.get(ev));
        return result;
    }

    public Field.IntFv intVfTransfer(Field.IntVf intVf){
        Field.IntFv result = new Field.IntFv(medium);
        for (Vf vf : intVf.keySet())
            result.put(vf.getDual(), intVf.get(vf));
        return result;
    }

    public Field.IntVf intFvTransfer(Field.IntFv intFv){
        Field.IntVf result = new Field.IntVf(medium);
        for (Fv fv : intFv.keySet())
            result.put(fv.getDual(), intFv.get(fv));
        return result;
    }

    public Field.IntEf intFeTransfer(Field.IntFe intFe){
        Field.IntEf result = new Field.IntEf(medium);
        for (Fe fe : intFe.keySet())
            result.put(fe.getDual(), intFe.get(fe));
        return result;
    }

    public Field.IntFe intEfTransfer(Field.IntEf intEf){
        Field.IntFe result = new Field.IntFe(medium);
        for (Ef ef : intEf.keySet())
            result.put(ef.getDual(), intEf.get(ef));
        return result;
    }
}
