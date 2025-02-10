package local.furthestpointoptimization.model.miseEnBoite;

import local.furthestpointoptimization.model.vertexSets.Vertex;
import local.furthestpointoptimization.model.vertexSets.VertexSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class CalculEfficiency {

    // Coord[][][] boite;

    // public CalculEfficiency(VertexSet vs) {}

    // X --------->
    // | j
    // | i
    // |
    // v

    //
    // MeB : [0..line][0..col] -> Set[Optionnal<Vertex>]
    // h : Vertex -> (i,j)

    HashMap<Vertex, Coord> vertexToCoord;
    HashMap<Coord, Vertex> coordToVertex;

    private void reverse(){
        coordToVertex = new HashMap<>();
        for (Vertex vertex : vertexToCoord.keySet())
            coordToVertex.put(vertexToCoord.get(vertex), vertex);
    }

    public CalculEfficiency(HashMap<Vertex, Coord> vertexToCoord) {
        this.vertexToCoord = vertexToCoord;
        reverse();
    }

    public CalculEfficiency(MiseEnBoite meb, VertexSet vs) {
        vertexToCoord = new HashMap<>(meb.miseEnBoite(vs));
        reverse();
    }

    private double Ti(ArrayList<Vertex> line){
        if (line.isEmpty()) return 0;

        HashSet<Coord> set = new HashSet<>();
        for (Vertex vertex : line) for (Vertex neighbor : vertex.getNeighbors())
            set.add(Coord.minus(vertexToCoord.get(vertex), vertexToCoord.get(neighbor)));
        return set.size()/6.;
    }

    public double T(){
        if (vertexToCoord.isEmpty()) return 0;

        int yMax = 0, xMax = 0;
        for (Coord coord : coordToVertex.keySet()){
            if (coord.getI() > yMax) yMax = coord.getI();
            if (coord.getJ() > xMax) xMax = coord.getJ();
        }
        yMax++; xMax++;

        ArrayList<ArrayList<Vertex>> lines = new ArrayList<>(yMax);
        for (int i = 0; i < yMax; i++){
            ArrayList<Vertex> line = new ArrayList<>(xMax);
            for (Vertex vertex: vertexToCoord.keySet())
                if (vertexToCoord.get(vertex).getI() == i && !vertex.isBorder()) line.add(vertex);
            if (!line.isEmpty()) lines.add(line);
        }

        double sum = 0;
        for (ArrayList<Vertex> line: lines) sum += Ti(line);
        return sum/lines.size();
    }
}
