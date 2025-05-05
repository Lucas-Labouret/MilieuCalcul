package local.computingMedia.cannings.evaluation;

import local.computingMedia.cannings.Canning;
import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.cannings.coords.tCoords.*;
import local.computingMedia.sLoci.Vertex;
import local.computingMedia.tLoci.Ev;
import local.computingMedia.tLoci.Fe;
import local.computingMedia.tLoci.Vf;

import java.util.HashMap;
import java.util.HashSet;

public class MasksComputer {
    private Canning canning;

    public MasksComputer(Canning canning) {
        this.canning = canning;
    }

    public void setCanning(Canning canning) { this.canning = canning; }

    public int[] getDeltas(){
        HashMap<Vertex, VertexCoord> vCanning = canning.getVertexCanning();

        int deltaY = 0, deltaX = 0;
        for (Vertex v : vCanning.keySet()) for (Vertex neighbor : v.getNeighbors()) {
            deltaY = Math.max(deltaY, Math.abs(vCanning.get(v).Y() - vCanning.get(neighbor).Y()));
            deltaX = Math.max(deltaX, Math.abs(vCanning.get(v).X() - vCanning.get(neighbor).X()));
        }
        int upperBound = (2*deltaY+1) * (2*deltaX+1);
        return new int[]{deltaY, deltaX, upperBound};
    }

    private HashMap<Integer, HashSet<MaskIndex>> maskSetVeEv(){
        HashMap<Integer, HashSet<MaskIndex>> maskSet = new HashMap<>();
        for (Ev ev : canning.getEv()) {
            EvCoord coord = canning.getEvCanning().get(ev);
            VeCoord facingCoord = canning.getVeCanning().get(canning.getEvVeCommunication().get(ev));

            MaskIndex mask = new MaskIndex(
                    coord.side(),
                    coord.edge().theta() - facingCoord.theta(),
                    coord.edge().vertex().Y() - facingCoord.vertex().Y(),
                    coord.edge().vertex().X() - facingCoord.vertex().X()
            );

            int line = coord.edge().vertex().Y();
            maskSet.computeIfAbsent(line, k -> new HashSet<>()).add(mask);
        }
        return maskSet;
    }
    public double getAverageVeEv(){
        HashMap<Integer, HashSet<MaskIndex>> maskSets = maskSetVeEv();
        int accumulator = 0;
        for (HashSet<MaskIndex> maskSet : maskSets.values()) {
            accumulator += maskSet.size();
        }
        return accumulator / (double)(maskSets.size());
    }
    public int getMaxVeEv(){
        HashMap<Integer, HashSet<MaskIndex>> maskSets = maskSetVeEv();
        int max = 0;
        for (HashSet<MaskIndex> maskSet : maskSets.values()) {
            if (maskSet.size() > max) max = maskSet.size();
        }
        return max;
    }

    private HashMap<Integer, HashSet<MaskIndex>> maskSetVfFv(){
        HashMap<Integer, HashSet<MaskIndex>> maskSet = new HashMap<>();
        for (Vf vf : canning.getVf()) {
            VfCoord coord = canning.getVfCanning().get(vf);
            FvCoord facingCoord = canning.getFvCanning().get(canning.getVfFvCommunication().get(vf));

            MaskIndex mask = new MaskIndex(
                    facingCoord.side(),
                    facingCoord.face().theta() - coord.theta(),
                    facingCoord.face().vertex().Y() - coord.vertex().Y(),
                    facingCoord.face().vertex().X() - coord.vertex().X()
            );

            int line = coord.vertex().Y();
            maskSet.computeIfAbsent(line, k -> new HashSet<>()).add(mask);
        }
        return maskSet;
    }
    public double getAverageVfFv(){
        HashMap<Integer, HashSet<MaskIndex>> maskSets = maskSetVfFv();
        int accumulator = 0;
        for (HashSet<MaskIndex> maskSet : maskSets.values()) {
            accumulator += maskSet.size();
        }
        return accumulator / (double)(maskSets.size());
    }
    public int getMaxVfFv(){
        HashMap<Integer, HashSet<MaskIndex>> maskSets = maskSetVfFv();
        int max = 0;
        for (HashSet<MaskIndex> maskSet : maskSets.values()) {
            if (maskSet.size() > max) max = maskSet.size();
        }
        return max;
    }

    private HashMap<Integer, HashSet<MaskIndex>> maskSetEfFe(){
        HashMap<Integer, HashSet<MaskIndex>> maskSet = new HashMap<>();
        for (Fe fe : canning.getFe()) {
            FeCoord coord = canning.getFeCanning().get(fe);
            EfCoord facingCoord = canning.getEfCanning().get(canning.getFeEfCommunication().get(fe));

            MaskIndex mask = new MaskIndex(
                    coord.side() - facingCoord.side(),
                    coord.face().theta() - facingCoord.edge().theta(),
                    coord.face().vertex().Y() - facingCoord.edge().vertex().Y(),
                    coord.face().vertex().X() - facingCoord.edge().vertex().X()
            );
            int line = coord.face().vertex().Y();
            maskSet.computeIfAbsent(line, k -> new HashSet<>()).add(mask);

            System.out.println(coord + " -> " + facingCoord + " = " + mask);
        }
        return maskSet;
    }
    public double getAverageEfFe(){
        HashMap<Integer, HashSet<MaskIndex>> maskSets = maskSetEfFe();
        int accumulator = 0;
        for (HashSet<MaskIndex> maskSet : maskSets.values()) {
            accumulator += maskSet.size();
        }
        return accumulator / (double)(maskSets.size());
    }
    public int getMaxEfFe(){
        HashMap<Integer, HashSet<MaskIndex>> maskSets = maskSetEfFe();
        int max = 0;
        for (HashSet<MaskIndex> maskSet : maskSets.values()) {
            if (maskSet.size() > max) max = maskSet.size();
        }
        return max;
    }
}
