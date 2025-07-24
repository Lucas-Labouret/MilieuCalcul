package cannings.vertexCannings;

import cannings.coords.sCoords.VertexCoord;
import computingMedia.media.Medium;
import computingMedia.sLoci.Vertex;

import java.util.HashMap;

/**
 * RoundedCoordDichotomyVCanning is a vertex canning implementation that scales the spatial coordinates of each vertex
 * by a given factor then rounds them, to the nearest integer values to use as canning coordinates.
 * <p>
 * It searches for an optimal scaling factor by incrementally increasing the scale until all vertices can be uniquely mapped to their coordinates.
 */
public class RoundedCoordIncremental2DVCanning implements VertexCanning {
    private HashMap<Vertex, VertexCoord> vertexCanning = null;
    private final Medium medium;

    private int width = 0;
    private int height = 0;

    private final double increment;
    private double scaleY = 1;
    private double scaleX = 1;

    public RoundedCoordIncremental2DVCanning(Medium medium) {
        this(medium, 1);
    }

    public RoundedCoordIncremental2DVCanning(Medium medium, double increment) {
        this.medium = medium;
        this.increment = increment;
    }

    public double getIncrement() {
        return increment;
    }
    public double getScaleY() { return scaleY; }
    public double getScaleX() { return scaleX; }

    @Override public Medium getMedium() { return medium; }

    @Override public int getWidth() { return width; }
    @Override public int getHeight() { return height; }

    @Override public HashMap<Vertex, VertexCoord> getVertexCanning() { return this.vertexCanning; }

    private HashMap<VertexCoord, Vertex> getCoordVertexMap(Medium medium){
        HashMap<VertexCoord, Vertex> coordVertexMap = new HashMap<>();

        while (true){
            for (scaleX = 1; scaleX <= scaleY; scaleX+=increment) {
                boolean exit = true;
                for (Vertex vertex : medium) {
                    VertexCoord coord = new VertexCoord(
                            (int) Math.round(scaleY * vertex.getY() * medium.getHeight()),
                            (int) Math.round(scaleX * vertex.getX() * medium.getWidth())
                    );
                    if (coordVertexMap.get(coord) == null) coordVertexMap.put(coord, vertex);
                    else {
                        exit = false;
                        break;
                    }
                }
                if (exit) return coordVertexMap;
                coordVertexMap.clear();
            }
            scaleY+=increment;
        }
    }

    @Override
    public void can(){
        HashMap<VertexCoord, Vertex> coordVertexMap = getCoordVertexMap(medium);

        int yMax = 0, xMax = 0;
        HashMap<Vertex, VertexCoord> result = new HashMap<>();
        for (VertexCoord coord : coordVertexMap.keySet()){
            if (coord.Y() > yMax) yMax = coord.Y();
            if (coord.X() > xMax) xMax = coord.X();
            result.put(coordVertexMap.get(coord), coord);
        }

        width = xMax+1;
        height = yMax+1;
        vertexCanning = result;
    }
}
