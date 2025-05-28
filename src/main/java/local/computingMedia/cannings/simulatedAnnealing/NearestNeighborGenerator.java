package local.computingMedia.cannings.simulatedAnnealing;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.cannings.vertexCannings.SimpleVertexCanning;
import local.computingMedia.cannings.vertexCannings.VertexCanning;
import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.Edge;
import local.computingMedia.sLoci.Vertex;
import local.misc.WeightedRandomCollection;
import local.misc.simulatedAnnealing.RandomNeighborGenerator;

import java.util.HashMap;

public class NearestNeighborGenerator implements RandomNeighborGenerator<VertexCanning, Medium> {
    private final WeightedRandomCollection<VertexCanning> potentialNeighbors = new WeightedRandomCollection<>();

    private VertexCanning candidate;
    private Medium environment;

    private HashMap<Vertex, VertexCoord> vertexToCoord;
    private HashMap<VertexCoord, Vertex> coordToVertex;

    private final HashMap<VertexCanning , Double> distances = new HashMap<>();

    @Override
    public VertexCanning generate(VertexCanning candidate, Medium environment) {
        potentialNeighbors.clear();
        distances.clear();
        this.candidate = candidate;
        this.environment = environment;

        HashMap<Vertex, VertexCoord> vertexToCoord = candidate.getVertexCanning();
        HashMap<VertexCoord, Vertex> coordToVertex = new HashMap<>();
        for (Vertex vertex : vertexToCoord.keySet()) {
            VertexCoord coord = vertexToCoord.get(vertex);
            coordToVertex.put(coord, vertex);
        }

        this.vertexToCoord = vertexToCoord;
        this.coordToVertex = coordToVertex;

        for (Vertex vertex : environment){
            VertexCoord coord = vertexToCoord.get(vertex);

            VertexCoord north = new VertexCoord(coord.Y()-1, coord.X()     );
            VertexCoord south = new VertexCoord(coord.Y()+1, coord.X()     );
            VertexCoord east  = new VertexCoord(coord.Y()     , coord.X()+1);
            VertexCoord west  = new VertexCoord(coord.Y()     , coord.X()-1);

            addNeighborNS(vertex, north);
            addNeighborNS(vertex, south);

            addNeighborEW(vertex, east);
            addNeighborEW(vertex, west);
        }
        buildPotentialNeighbors();
        return potentialNeighbors.next();
    }

    private void addSwap(Vertex vertex, VertexCoord neighborCoord) {
        Vertex neighbor = coordToVertex.get(neighborCoord);

        SimpleVertexCanning neighborCanning = new SimpleVertexCanning(
                (HashMap<Vertex, VertexCoord>) vertexToCoord.clone(),
                candidate.getWidth(), candidate.getHeight()
        );
        neighborCanning.getVertexCanning().put(vertex, neighborCoord);
        neighborCanning.getVertexCanning().put(neighbor, vertexToCoord.get(vertex));

        double distance = vertex.distanceFrom(neighbor);
        distances.put(neighborCanning, distance);
    }

    private void addNeighborNS(Vertex vertex, VertexCoord neighborCoord) {
        if (neighborCoord.Y() < 0 || neighborCoord.Y() >= candidate.getHeight()) return;

        if (coordToVertex.containsKey(neighborCoord)) return; //addSwap(vertex, neighborCoord);
        else {
            Vertex leftend = null;
            Vertex rightend = null;

            int endIndex = neighborCoord.X();
            while (endIndex >= 0){
                VertexCoord left = new VertexCoord(neighborCoord.Y(), endIndex);
                if (coordToVertex.containsKey(left)){
                    leftend = coordToVertex.get(left);
                    break;
                }
                endIndex--;
            }

            endIndex = neighborCoord.X();
            while (endIndex < environment.getWidth()){
                VertexCoord right = new VertexCoord(neighborCoord.Y(), endIndex);
                if (coordToVertex.containsKey(right)){
                    rightend = coordToVertex.get(right);
                    break;
                }
                endIndex++;
            }

            if (leftend == null && rightend == null) return;

            SimpleVertexCanning neighborCanning = new SimpleVertexCanning(
                    (HashMap<Vertex, VertexCoord>) vertexToCoord.clone(),
                    candidate.getWidth(), candidate.getHeight()
            );
            neighborCanning.getVertexCanning().put(vertex, neighborCoord);

            if (leftend == null){
                leftend = new Vertex(rightend.getX()-1, rightend.getY());
            }
            if (rightend == null){
                rightend = new Vertex(leftend.getX()+1, leftend.getY());
            }

            double distance = vertex.distanceFrom(new Edge(leftend, rightend));
            distances.put(neighborCanning, distance);
        }
    }

    private void addNeighborEW(Vertex vertex, VertexCoord neighborCoord) {
        if (neighborCoord.X() < 0 || neighborCoord.X() >= candidate.getWidth()) return;

        if (coordToVertex.containsKey(neighborCoord)) return; //addSwap(vertex, neighborCoord);
        else {
            Vertex upend = null;
            Vertex lowend = null;

            int endIndex = neighborCoord.Y();
            while (endIndex >= 0) {
                VertexCoord up = new VertexCoord(endIndex, neighborCoord.X());
                if (coordToVertex.containsKey(up)) {
                    upend = coordToVertex.get(up);
                    break;
                }
                endIndex--;
            }

            endIndex = neighborCoord.Y();
            while (endIndex < environment.getWidth()) {
                VertexCoord low = new VertexCoord(endIndex, neighborCoord.X());
                if (coordToVertex.containsKey(low)) {
                    lowend = coordToVertex.get(low);
                    break;
                }
                endIndex++;
            }

            if (upend == null && lowend == null) return;

            SimpleVertexCanning neighborCanning = new SimpleVertexCanning(
                    (HashMap<Vertex, VertexCoord>) vertexToCoord.clone(),
                    candidate.getWidth(), candidate.getHeight()
            );
            neighborCanning.getVertexCanning().put(vertex, neighborCoord);

            if (upend == null) {
                upend = new Vertex(lowend.getX(), lowend.getY()+1);
            }
            if (lowend == null) {
                lowend = new Vertex(upend.getX(), upend.getY()-1);
            }

            double distance = vertex.distanceFrom(new Edge(upend, lowend));
            distances.put(neighborCanning, distance);
        }
    }

    private double convexFunction(double weight) {
        //return ( Math.exp(weight)-1 )/( Math.E-1 );
        return weight*weight;
    }
    private double makeConvexer(double weight, int iter) {
        for (int i = 0; i < iter; i++) weight = convexFunction(weight);
        return weight;
    }

    private void buildPotentialNeighbors() {
        double maxDistance = distances.values().stream().max(Double::compare).get();
        double averageDistance = distances.values().stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        if (maxDistance == 0) return;
        for (VertexCanning neighbor : distances.keySet()) {
            double distance = distances.get(neighbor);
            double weight = makeConvexer(1 - distance/maxDistance, 3);
            if (weight < averageDistance/(3*maxDistance)) continue;
            potentialNeighbors.add(weight, neighbor);
        }
    }
}
