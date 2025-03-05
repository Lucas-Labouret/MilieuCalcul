package local.computingMedium.media;

import local.computingMedium.Vertex;

@SuppressWarnings("serial")
public abstract class HardBorderedMedium extends Medium {
    public HardBorderedMedium() {}

    public HardBorderedMedium(Vertex... vertices) {
        super(vertices);
    }

    public abstract boolean isInBorder(Vertex vertex);
}
