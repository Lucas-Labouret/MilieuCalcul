package local.furthestpointoptimization.model.miseEnBoite;

import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;



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

    HashMap<Vertex, Coord> hm;

    public CalculEfficiency(HashMap<Vertex, Coord> hm) {
        this.hm = hm;
    }

    public CalculEfficiency(MiseEnBoite meb, VertexSet vs) {
        hm = new HashMap<>(meb.miseEnBoite(vs));
    }

    Coord h(Vertex v) {
        return hm.get(v);
    }

    public int Tk(int k) {
        HashSet<Coord> ens = new HashSet<>();
        for (Vertex v : hm.keySet()) {
            Optional<Vertex> vopt = v.getKNeighbor(k);
            if (vopt.isPresent()) {
                ens.add(Coord.minus(h(vopt.get()), h(v)));
            }
        }
        return ens.size();
    }

    public double Tmoy() {
        int maxNbVoisins = 0;
        for (Vertex vertex : hm.keySet()) {
            int count = vertex.getNeighbors().size();
            if (count > maxNbVoisins)
                maxNbVoisins = count;
        }
        ArrayList<Integer> s = new ArrayList<>();
        for (int k = 0; k < maxNbVoisins; ++k) {
            s.add(Tk(k));
        }
        return maxNbVoisins;
    }

}
