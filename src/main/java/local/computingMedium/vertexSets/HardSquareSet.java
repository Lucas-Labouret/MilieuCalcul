package local.computingMedium.vertexSets;

import java.io.Serial;
import java.util.ArrayList;

import local.computingMedium.Vertex;

public class HardSquareSet extends VertexSet {
    @Serial private static final long serialVersionUID = -4755063321120367500L;

    public HardSquareSet(int vertexWidth, int count) {
        if (vertexWidth < 2) throw new IllegalArgumentException("Vertex width must be at least 2");
        vertexWidth--;

        hardBorder = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            double x = Math.random();
            double y = Math.random();
            add(new Vertex(x, y));
        }

        ArrayList<Vertex> topBorder = new ArrayList<>();
        ArrayList<Vertex> bottomBorder = new ArrayList<>();
        ArrayList<Vertex> leftBorder = new ArrayList<>();
        ArrayList<Vertex> rightBorder = new ArrayList<>();

        topBorder.add(new Vertex(0, 0, true, true, true, false, false));
        rightBorder.add(new Vertex(1, 0, true, true, false, true, false));
        bottomBorder.add(new Vertex(1, 1, true, false, false, true, true));
        leftBorder.add(new Vertex(0, 1, true, false, true, false, true));

        for (int i = 1; i < vertexWidth; i++) {
            topBorder.add(new Vertex(i/(double)vertexWidth, 0, true, true, false, false, false));
            rightBorder.add(new Vertex(1, i/(double)vertexWidth, true, false, false, true, false));
            bottomBorder.add(new Vertex(1-i/(double)vertexWidth, 1, true, false, false, false, true));
            leftBorder.add(new Vertex(0, 1-i/(double)vertexWidth, true, false, true, false, false));
        }

        for (int i = 0; i < vertexWidth-1; i++) {
            topBorder.get(i).addNeighbor(topBorder.get(i+1));
            add(topBorder.get(i));

            rightBorder.get(i).addNeighbor(rightBorder.get(i+1));
            add(rightBorder.get(i));

            bottomBorder.get(i).addNeighbor(bottomBorder.get(i+1));
            add(bottomBorder.get(i));

            leftBorder.get(i).addNeighbor(leftBorder.get(i+1));
            add(leftBorder.get(i));
        }

        add(topBorder.getLast());
        add(rightBorder.getLast());
        add(bottomBorder.getLast());
        add(leftBorder.getLast());

        topBorder.getLast().addNeighbor(rightBorder.getFirst());
        rightBorder.getLast().addNeighbor(bottomBorder.getFirst());
        bottomBorder.getLast().addNeighbor(leftBorder.getFirst());
        leftBorder.getLast().addNeighbor(topBorder.getFirst());

        hardBorder.addAll(topBorder);
        hardBorder.addAll(rightBorder);
        hardBorder.addAll(bottomBorder);
        hardBorder.addAll(leftBorder);
    }

    private void bandageBorderFix() {
        for (int i = 1; i < hardBorder.size()-1; i++) {
            Vertex vertex = hardBorder.get(i);

            ArrayList<Vertex> neighbors = new ArrayList<>(vertex.getNeighbors());
            for (Vertex neighbor : neighbors) {
                if (!neighbor.isBorder()) continue;

                if (vertex.isTopBorder() && neighbor.isTopBorder()) {
                    if (neighbor != hardBorder.get(i - 1) && neighbor != hardBorder.get(i + 1))
                        vertex.removeNeighbor(neighbor);
                }

                if (vertex.isRightBorder() && neighbor.isRightBorder()) {
                    if (neighbor != hardBorder.get(i - 1) && neighbor != hardBorder.get(i + 1))
                        vertex.removeNeighbor(neighbor);
                }

                if (vertex.isBottomBorder() && neighbor.isBottomBorder()) {
                    if (neighbor != hardBorder.get(i - 1) && neighbor != hardBorder.get(i + 1))
                        vertex.removeNeighbor(neighbor);
                }

                if (vertex.isLeftBorder() && neighbor.isLeftBorder()) {
                    if (neighbor != hardBorder.get(i - 1) && neighbor != hardBorder.get(i + 1))
                        vertex.removeNeighbor(neighbor);
                }
            }
        }
    }

    @Override
    public void delaunayTriangulate() {
        super.delaunayTriangulate();
        bandageBorderFix();
    }

    @Override
    public boolean isInBorder(Vertex vertex) {
        return 0 < vertex.getX() && vertex.getX() < getWidth()  &&
               0 < vertex.getY() && vertex.getY() < getHeight();
    }
}
