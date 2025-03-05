package local.computingMedia.media;

import local.computingMedia.Vertex;

@SuppressWarnings("serial")
public class SoftSquareMedium extends SoftBorderedMedium {
    public SoftSquareMedium() {}

    public SoftSquareMedium(Vertex... vertices) {
        super(vertices);
    }

    public SoftSquareMedium copy() {
        return new SoftSquareMedium(this.toArray(new Vertex[0]));
    }

    public SoftSquareMedium(int count) {
        for (int i = 0; i < count; i++) {
            double x = Math.random();
            double y = Math.random();
            this.add(new Vertex(x,y));
        }
    }

    @Override
    public boolean isInBorder(Vertex vertex) {
        return 0 <= vertex.getX() && vertex.getX() <= 1 &&
               0 <= vertex.getY() && vertex.getY() <= 1;
    }
}
