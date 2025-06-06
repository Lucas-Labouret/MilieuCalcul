package local.computingMedia.media;

import local.computingMedia.sLoci.Vertex;
import local.misc.linkedList.LinkedList;
import local.misc.linkedList.Node;

import java.util.ArrayList;

/**
 * A medium with a soft border.
 * <p>
 * The vertices of the border are not predefined. Instead, the border by correcting the convex hull of the medium,
 * eliminating edges that are too long and replacing them with the outermost edges remaining.
 */
@SuppressWarnings("serial")
public abstract class SoftBorderedMedium extends Medium {
    public SoftBorderedMedium() {}

    public SoftBorderedMedium(Vertex... vertices) {
        super(vertices);
    }

    protected void setSoftBorder() {
        if (this.isEmpty()) return;
        softBorder = new LinkedList<>();

        // Find the leftmost vertex
        Vertex current = this.iterator().next();
        while (true){
            Vertex next = null;
            for (Vertex neighbour : current.getNeighbors()) {
                if (next == null && neighbour.getX() < current.getX()) {
                    next = neighbour;
                } else if (next != null && neighbour.getX() < next.getX()) {
                    next = neighbour;
                }
            }

            if (next == null) break;
            current = next;
        }
        softBorder.addFirst(current);
        Node<Vertex> currentNode = softBorder.head;

        ArrayList<Vertex> neighbors = new ArrayList<>(current.getNeighbors());
        final Vertex tmp = current;
        neighbors.sort((v1, v2) -> new ClockWise(tmp).compare(v1, v2));
        for (int i = 0; i < neighbors.size(); i++) {
            if (Vertex.getAngle(neighbors.get(i), current, neighbors.get((i+1)%neighbors.size())) < Math.PI) {
                softBorder.head.addNext(neighbors.get((i+1)%neighbors.size()));
                break;
            }
        }

        while (true) {
            ArrayList<Vertex> sortedNeighbors = new ArrayList<>(currentNode.next.value.getNeighbors());
            final Vertex tmp2 = currentNode.next.value;
            sortedNeighbors.sort((v1, v2) -> new ClockWise(tmp2).compare(v1, v2));

            int index = sortedNeighbors.indexOf(currentNode.value);
            int nextIndex = (index + 1) % sortedNeighbors.size();

            if (sortedNeighbors.get(nextIndex) == softBorder.head.value) break;

            currentNode = currentNode.next;
            currentNode.addNext(sortedNeighbors.get(nextIndex));
        }

        correctHull();
    }

    private void correctHull() {
        Node<Vertex> current = softBorder.head;
        while (current != null) {
            Node<Vertex> next = current.next;
            if (next == null) next = softBorder.head;
            Vertex commonNeighbor = null;
            for (Vertex neighbor : current.value.getNeighbors()) {
                if (next.value.getNeighbors().contains(neighbor)) {
                    commonNeighbor = neighbor;
                    break;
                }
            }
            double angle;
            try{ angle = Vertex.getAngle(current.value, commonNeighbor, next.value); }
            catch (NullPointerException e) {
                current = current.next;
                continue;
            }
            if (0.75 * Math.PI < angle && angle < 1.25 * Math.PI) {
                current.value.removeNeighbor(next.value);
                current.addNext(commonNeighbor);
            } else {
                current = current.next;
            }
        }
    }

    @Override
    public void delaunayTriangulate() {
        super.delaunayTriangulate();
        setSoftBorder();
    }
}