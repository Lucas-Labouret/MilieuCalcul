package local.computingMedia.cannings.vertexCannings;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.Vertex;

import java.util.HashMap;

/**
 * RoundedCoordDichotomyVCanning is a vertex canning implementation that scales the spatial coordinates of each vertex
 * by a given factor then rounds them, to the nearest integer values to use as canning coordinates.
 * <p>
 * It searches for an optimal scaling factor by incrementally increasing the scale until all vertices can be uniquely mapped to their coordinates.
 */
public class RoundedCoordIncrementalVCanning implements VertexCanning {
    private HashMap<Vertex, VertexCoord> vertexCanning = null;
    private Medium medium;

    private int width = 0;
    private int height = 0;

    private final double increment;

    public RoundedCoordIncrementalVCanning() {
        this(1);
    }

    public RoundedCoordIncrementalVCanning(double increment) {
        this.increment = increment;
    }

    @Override public int getWidth() { return width; }
    @Override public int getHeight() { return height; }

    @Override public void setMedium(Medium medium) { this.medium = medium; }
    @Override public HashMap<Vertex, VertexCoord> getVertexCanning() { return this.vertexCanning; }

    private HashMap<VertexCoord, Vertex> getCoordVertexMap(Medium medium){
        HashMap<VertexCoord, Vertex> coordVertexMap = new HashMap<>();

        double scale = 1;
        while (true){
            boolean exit = true;
            for (Vertex vertex : medium){
                VertexCoord coord = new VertexCoord(
                        (int) Math.round(scale*vertex.getY()),
                        (int) Math.round(scale*vertex.getX())
                );
                if (coordVertexMap.get(coord) == null) coordVertexMap.put(coord, vertex);
                else {
                    exit = false;
                    break;
                }
            }
            if (exit) return coordVertexMap;
            coordVertexMap.clear();
            scale+=increment;
        }
    }

    @Override
    public void can(){
        HashMap<VertexCoord, Vertex> coordVertexMap = getCoordVertexMap(medium);

        int yMax = 0, xMax = 0;
        for (VertexCoord coord : coordVertexMap.keySet()){
            if (coord.Y() > yMax) yMax = coord.Y();
            if (coord.X() > xMax) xMax = coord.X();
        }

        width = xMax+1;
        height = yMax+1;

        HashMap<Vertex, VertexCoord> result = new HashMap<>();
        for (VertexCoord coord : coordVertexMap.keySet()){
            result.put(coordVertexMap.get(coord), coord);
        }
        vertexCanning = result;
    }
}
