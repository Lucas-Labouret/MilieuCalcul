package savefileManagers;

import computingMedia.media.SoftSquareMedium;

public class SoftSquareManager extends SavefileManager {
    @Override
    public SoftSquareMedium makeMedium() {
        return new SoftSquareMedium();
    }
}
