package local.ui.savefileManager;

import local.computingMedium.media.HardRectangleMedium;
import local.ui.mediumScene.MediumScene;
import local.ui.view.InformationBar;

public class HardRectangleManager extends SavefileManager {
    public HardRectangleManager(MediumScene scene, InformationBar info) {
        super(scene, info);
    }

    @Override
    public HardRectangleMedium makeVertexSet() {
        return new HardRectangleMedium();
    }
}
