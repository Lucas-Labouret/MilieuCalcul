package local.computingMedia.cannings;

import local.computingMedia.cannings.Coords.sCoords.VertexCoord;
import local.computingMedia.sLoci.Vertex;
import local.computingMedia.media.Medium;

import java.util.HashMap;

public class RoundedCoordVCanning implements VertexCanning {
    private HashMap<Vertex, VertexCoord> vertexCanning = null;
    private Medium medium;

    public void setMedium(Medium medium) { this.medium = medium; }
    public HashMap<Vertex, VertexCoord> getVertexCanning() { return this.vertexCanning; }

    private HashMap<VertexCoord, Vertex> getCoordVertexMap(Medium medium){
        HashMap<VertexCoord, Vertex> coordVertexMap = new HashMap<>();

        int scale = 1;
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
            scale++;
        }
    }

    private boolean xOverlap(Vertex[][] grid, int y1, int y2, int xMax){
        for (int x = 0; x < xMax; x++)
            if (grid[y1][x] != null && grid[y2][x] != null) return true;
        return false;
    }

    private boolean yOverlap(Vertex[][] grid, int x1, int x2, int yMax){
        for (int y = 0; y < yMax; y++)
            if (grid[y][x1] != null && grid[y][x2] != null) return true;
        return false;
    }

    private void collapse(Vertex[][] grid, int yMax, int xMax){
        int y = 0, x = 0;
        while (y+1 < yMax || x+1 < xMax){
            if (y + 1 < yMax) {
                if (!xOverlap(grid, y, y+1, xMax)){
                    for (int y_ = y+1; y_ < yMax; y_++) for (int x_ = 0; x_ < xMax; x_++) { 
                        grid[y_-1][x_] = grid[y_][x_];
                        grid[y_][x_] = null;
                    }
                    yMax--;
                } else y++;
            }

            if (x + 1 < xMax) {
                if (!yOverlap(grid, x, x+1, yMax)){
                    for (int x_ = x+1; x_ < xMax; x_++) for (int y_ = 0; y_ < yMax; y_++) {
                        grid[y_][x_-1] = grid[y_][x_];
                        grid[y_][x_] = null;                        
                    }
                    xMax--;
                } else x++;
            }
        }
    }
    
    @Override
    public void can(){
        HashMap<VertexCoord, Vertex> coordVertexMap = getCoordVertexMap(medium);

        int yMax = 0, xMax = 0;
        for (VertexCoord coord : coordVertexMap.keySet()){
            if (coord.X() > yMax) yMax = coord.X();
            if (coord.Y() > xMax) xMax = coord.Y();
        }
        yMax++; xMax++;

        Vertex[][] grid = new Vertex[yMax][xMax];
        for (VertexCoord coord : coordVertexMap.keySet()){
            grid[coord.X()][coord.Y()] = coordVertexMap.get(coord);
        }
        //collapse(grid, yMax, xMax);

        HashMap<Vertex, VertexCoord> result = new HashMap<>();
        for (int y = 0; y < yMax; y++) for (int x = 0; x < xMax; x++){
            if (grid[y][x] != null) result.put(grid[y][x], new VertexCoord(y, x));
        }
        vertexCanning = result;
    }
}