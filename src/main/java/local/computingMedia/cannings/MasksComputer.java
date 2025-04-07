package local.computingMedia.cannings;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.sLoci.Vertex;

import java.util.HashMap;

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
}
