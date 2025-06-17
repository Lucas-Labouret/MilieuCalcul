package savefileManagers;

import computingMedia.media.HardRectangleMedium;

public class HardRectangleManager extends SavefileManager {
    @Override
    public HardRectangleMedium makeMedium() {
        return new HardRectangleMedium();
    }
}
