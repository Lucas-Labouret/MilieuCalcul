package savefileManagers;

import computingMedia.media.SoftCircleMedium;

public class SoftCircleManager extends SavefileManager {
    @Override
    public SoftCircleMedium makeMedium() {
        return new SoftCircleMedium();
    }
}
