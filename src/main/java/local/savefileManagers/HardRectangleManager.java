package local.savefileManagers;

import local.computingMedia.media.HardRectangleMedium;

public class HardRectangleManager extends SavefileManager {
    @Override
    public HardRectangleMedium makeMedium() {
        return new HardRectangleMedium();
    }
}
