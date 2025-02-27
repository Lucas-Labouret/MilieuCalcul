package local.computingMedium.vertexSets;

import local.computingMedium.Vertex;
import local.misc.GeometricPrimitives;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class HardHexSet extends VertexSet {
    public HardHexSet() {}

    public HardHexSet(int borderWidth, int borderHeight, int count){
        borderHeight++;

        final double largeHeight = 1/Math.tan(Math.PI/6);

        width = borderWidth;
        height = (borderHeight/2.)*largeHeight;

        addBorder(
                borderWidth, borderHeight,
                largeHeight
        );

        for (int i = 0; i < count; i++){
            Vertex v;
            do v = new Vertex(Math.random()*width, Math.random()*height);
            while (!GeometricPrimitives.insidePolygon(v, getHardBorder()));
            add(v);
        }
        for (Vertex vertex : this){
            vertex.setX(vertex.getX()/width);
            vertex.setY(vertex.getY()/width);
        }
        width = 1;
        height = height/width;

        for (Vertex vertex : this){
            for (Vertex other : this){
                if (vertex == other) continue;
                if (vertex.almostEquals(other, 1e-10)){
                    throw new RuntimeException("Two vertices are too close");
                }
            }
        }
    }

    private void addBorder(int totalWidth, int totalHeight, double hexHeight){
        ArrayList<Vertex> border = new ArrayList<>();

        for (int i = 0; i < totalWidth; i++){
            Vertex vertex = new Vertex(i+0.5, 0, true, true, i == 0, i == totalWidth-1, false);
            if (!border.isEmpty())
                border.getLast().addNeighbor(vertex);
            border.add(vertex);
        }
        for (int i = 0; i < totalHeight-1; i++){
            Vertex vertex;
            if (i%2 == 0) vertex = new Vertex(totalWidth, (i+1)*hexHeight/2, true, false, false, true, i == totalHeight-2);
            else vertex = new Vertex(totalWidth-0.5, (i+1)*hexHeight/2, true, false, false, true, i == totalHeight-2);
            border.getLast().addNeighbor(vertex);
            border.add(vertex);
        }
        for (int i = totalWidth - (totalHeight%2 == 0 ? 1 : 0); i >= 0; i--){
            Vertex vertex;
            if (totalHeight%2 == 1) vertex = new Vertex(i, this.height, true, false, i == 0, i == totalWidth, true);
            else vertex = new Vertex(i+0.5, this.height, true, false, i == 0, i == totalWidth, true);
            border.getLast().addNeighbor(vertex);
            border.add(vertex);
        }
        for (int i = totalHeight-1; i > 0; i--){
            Vertex vertex;
            if (i%2 == 0) vertex = new Vertex(0.5, i*hexHeight/2, true, false, true, false, false);
            else vertex = new Vertex(0, i*hexHeight/2, true, false, true, false, false);
            border.getLast().addNeighbor(vertex);
            border.add(vertex);
        }
        border.getLast().addNeighbor(border.getFirst());

        this.addAll(border);
        this.hardBorder = border;
    }
    public void bandageBorderFix(){
        for (Vertex v1 : hardBorder) for (Vertex v2 : hardBorder){
            if (v1 == v2) continue;
            if (Math.abs(v1.getX() - v2.getX()) < 1e-4){
                v1.removeNeighbor(v2);
            }
        }
        hardBorder.getFirst().addNeighbor(hardBorder.getLast());

        for (int i = 0; i < hardBorder.size(); i+=1){
            Vertex v0 = hardBorder.get(i);
            Vertex v1 = hardBorder.get((i+1)% hardBorder.size());
            Vertex v2 = hardBorder.get((i+2)% hardBorder.size());

            System.out.println("1");

            if (!(v0.isLeftBorder() && v2.isLeftBorder()) && !(v0.isRightBorder() && v2.isRightBorder()))
                continue;

            System.out.println("2");
            if (v0.isLeftBorder() && v0.getX() < v1.getX()) continue;
            if (v0.isRightBorder() && v0.getX() > v1.getX()) continue;

            boolean hasTriangle = false;
            for (Vertex neighbor : v0.getNeighbors()){
                if (neighbor.isBorder()) continue;
                if (neighbor.getNeighbors().contains(v1)){
                    hasTriangle = true;
                    break;
                }
            }
            for (Vertex neighbor : v2.getNeighbors()){
                if (neighbor.isBorder()) continue;
                if (neighbor.getNeighbors().contains(v1)){
                    hasTriangle = true;
                    break;
                }
            }
            System.out.println(v0 + " " + v1 + " " + v2 + " " + hasTriangle);
            if (!hasTriangle) v0.addNeighbor(v2);
        }
    }

    @Override
    public boolean isInBorder(Vertex vertex) {
        return 0 <= vertex.getX() && vertex.getX() <= getWidth()  &&
               0 <= vertex.getY() && vertex.getY() <= getHeight() &&
               GeometricPrimitives.insidePolygon(vertex, getHardBorder());
    }
}
