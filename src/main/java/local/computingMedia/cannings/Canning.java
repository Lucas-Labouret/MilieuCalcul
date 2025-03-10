package local.computingMedia.cannings;

import local.computingMedia.cannings.Coords.sCoords.EdgeCoord;
import local.computingMedia.cannings.Coords.sCoords.FaceCoord;
import local.computingMedia.cannings.Coords.tCoords.*;
import local.computingMedia.sLoci.Edge;
import local.computingMedia.sLoci.Face;
import local.computingMedia.tLoci.*;

import java.util.HashMap;

public interface Canning extends VertexCanning{
    HashMap<Edge, EdgeCoord> getEdgeCanning();
    HashMap<Face, FaceCoord> getFaceCanning();
    HashMap<Ef, EfCoord> getEfCanning();
    HashMap<Ev, EvCoord> getEvCanning();
    HashMap<Fe, FeCoord> getFeCanning();
    HashMap<Fv, FvCoord> getFvCanning();
    HashMap<Ve, VeCoord> getVeCanning();
    HashMap<Vf, VfCoord> getVfCanning();
}
