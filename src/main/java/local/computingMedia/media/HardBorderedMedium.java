package local.computingMedia.media;

import local.computingMedia.geometry.Vertex;

@SuppressWarnings("serial")
public abstract class HardBorderedMedium extends Medium {
    public HardBorderedMedium() {}

    public HardBorderedMedium(Vertex... vertices) {
        super(vertices);
    }

    public abstract boolean isInBorder(Vertex vertex);
}
