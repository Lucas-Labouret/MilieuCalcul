package local.computingMedia.media;

import local.computingMedia.sLoci.Vertex;

/**
 * A medium with a hard border, meaning that it has a predefined border that cannot be moved.
 */
@SuppressWarnings("serial")
public abstract class HardBorderedMedium extends Medium {
    public HardBorderedMedium() {}

    public HardBorderedMedium(Vertex... vertices) {
        super(vertices);
    }

    public HardBorderedMedium(double width, double height, Vertex... vertices) {
        super(width, height, vertices);
    }

    public abstract boolean isInBorder(Vertex vertex);
}
