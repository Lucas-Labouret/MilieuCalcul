package local.furthestpointoptimization.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

class Coord {
    int i, j;
    Coord(int i, int j) {
        this.i=i;
        this.j=j;
    }

    public static Coord minus(Coord a, Coord b) {
        return new Coord(a.i-b.i, a.j-b.j);
    }

}

public class CalculEfficiency {

    // Coord[][][] boite;
    HashMap<Vertex, Coord> hm;
    
    public CalculEfficiency(VertexSet vs) {

    }



    //  X --------->
    //  |       j
    //  | i
    //  |
    //  v

    //
    // MeB : [0..line][0..col] -> Set[Optionnal<Vertex>]  
    // h : Vertex -> (i,j)  


    Coord h(Vertex v) {
        return hm.get(v);
    }

    public int Tk(int k) {
        HashSet<Coord> ens = new HashSet<Coord>();
        for (Vertex v: hm.keySet()) {
            Optional<Vertex> vopt = v.getKNeighbor(k);
            if (vopt.isPresent()) {
                ens.add(Coord.minus(h(vopt.get()), h(v)));
            }
        }
        return ens.size();
    }

    public double Tmoy() {
        int maxN = 0;
        for (Vertex vertex : hm.keySet()){
            int count = vertex.getNeighbors().size();
            if (count > maxN) maxN = count;
        }
        ArrayList<Integer> s = new ArrayList<>();
        for (int k=0;k<maxN;++k) {
            s.add(Tk(k));
        }
        return maxN;
    }






}
