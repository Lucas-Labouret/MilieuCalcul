package local.computingMedia.media;

import java.util.ArrayList;

import local.computingMedia.sLoci.Vertex;

@SuppressWarnings("serial")
public class HardRectangleMedium extends HardBorderedMedium {
    public HardRectangleMedium() {}

    public HardRectangleMedium(Vertex... vertices) {
        super(vertices);
    }

    public HardRectangleMedium copy() {
        return new HardRectangleMedium(this.toArray(new Vertex[0]));
    }

    public HardRectangleMedium(double width, int vertexWidth, int count) {
        if (vertexWidth < 2) throw new IllegalArgumentException("Vertex width must be at least 2");
        vertexWidth--;

        this.width = width;

        hardBorder = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            double x = Math.random()*width;
            double y = Math.random()*height;
            add(new Vertex(x, y));
        }

        ArrayList<Vertex> topBorder = new ArrayList<>();
        ArrayList<Vertex> bottomBorder = new ArrayList<>();
        ArrayList<Vertex> leftBorder = new ArrayList<>();
        ArrayList<Vertex> rightBorder = new ArrayList<>();

        topBorder.add(new Vertex(0, 0, true, true, true, false, false));
        rightBorder.add(new Vertex(width, 0, true, true, false, true, false));
        bottomBorder.add(new Vertex(width, height, true, false, false, true, true));
        leftBorder.add(new Vertex(0, height, true, false, true, false, true));

        for (int i = 1; i < vertexWidth; i++) {
            topBorder.add(new Vertex(width*i/vertexWidth, 0, true, true, false, false, false));
            rightBorder.add(new Vertex(width, height*i/vertexWidth, true, false, false, true, false));
            bottomBorder.add(new Vertex(width*(1-i/(double)vertexWidth), height, true, false, false, false, true));
            leftBorder.add(new Vertex(0, height*(1-i/(double)vertexWidth), true, false, true, false, false));
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
        return 0 < vertex.getX() && vertex.getX() < width  &&
               0 < vertex.getY() && vertex.getY() < height;
    }
}
