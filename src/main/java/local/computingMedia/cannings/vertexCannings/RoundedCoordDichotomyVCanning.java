package local.computingMedia.cannings.vertexCannings;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.sLoci.Vertex;
import local.computingMedia.media.Medium;

import java.util.HashMap;

/**
 * RoundedCoordDichotomyVCanning is a vertex canning implementation that scales the spatial coordinates of each vertex
 * by a given factor, then rounds them to the nearest integer values to use as canning coordinates.
 * <p>
 * It uses a dichotomy search to find an optimal scaling factor.
 * </p>
 */
public class RoundedCoordDichotomyVCanning implements VertexCanning {
    private HashMap<Vertex, VertexCoord> vertexCanning = null;
    private Medium medium;

    private int width = 0;
    private int height = 0;

    @Override public int getWidth() { return width; }
    @Override public int getHeight() { return height; }

    @Override public void setMedium(Medium medium) { this.medium = medium; }
    @Override public HashMap<Vertex, VertexCoord> getVertexCanning() { return this.vertexCanning; }

    private HashMap<VertexCoord, Vertex> getCoordVertexMap(Medium medium){
        HashMap<VertexCoord, Vertex> coordVertexMap = new HashMap<>();

        // Current bounds for the dichotomy search
        double topScale = 1;
        double botScale = 0;

        // First, we find an upper bound for the scaling factor
        while (true){
            boolean exit = true;
            for (Vertex vertex : medium){
                VertexCoord coord = new VertexCoord(
                    (int) Math.round(topScale*vertex.getY()),
                    (int) Math.round(topScale*vertex.getX())
                );
                if (coordVertexMap.get(coord) == null) coordVertexMap.put(coord, vertex);
                else {
                    exit = false;
                    break;
                } 
            }
            if (exit) break;
            coordVertexMap.clear();
            botScale = topScale;
            topScale *= 2;
        }

        // Now we perform a dichotomy search to find an optimal scaling factor
        // We stop when the difference between the bounds becomes small enough
        while (Math.abs(topScale - botScale) > 1e-6){
            double midScale = (topScale + botScale) / 2;
            HashMap<VertexCoord, Vertex> newCoordVertexMap = new HashMap<>();
            for (Vertex vertex : medium){
                VertexCoord coord = new VertexCoord(
                    (int) Math.round(midScale*vertex.getY()),
                    (int) Math.round(midScale*vertex.getX())
                );
                if (newCoordVertexMap.get(coord) == null) newCoordVertexMap.put(coord, vertex);
                else break;
            }
            if (newCoordVertexMap.size() == coordVertexMap.size()) {
                coordVertexMap = newCoordVertexMap;
                botScale = midScale;
            } else {
                topScale = midScale;
            }
        }

        return coordVertexMap;
    }
    
    @Override
    public void can(){
        HashMap<VertexCoord, Vertex> coordVertexMap = getCoordVertexMap(medium);

        HashMap<Vertex, VertexCoord> result = new HashMap<>();
        int yMax = 0, xMax = 0;
        for (VertexCoord coord : coordVertexMap.keySet()){
            result.put(coordVertexMap.get(coord), coord);
            if (coord.Y() > yMax) yMax = coord.Y();
            if (coord.X() > xMax) xMax = coord.X();
        }
        width = xMax+1;
        height = yMax+1;
        vertexCanning = result;
    }
}