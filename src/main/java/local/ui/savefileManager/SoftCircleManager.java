package local.ui.savefileManager;

import local.computingMedium.media.SoftCircleMedium;
import local.ui.mediumScene.MediumScene;
import local.ui.view.InformationBar;

public class SoftCircleManager extends SavefileManager {
    public SoftCircleManager(MediumScene scene, InformationBar info) {
        super(scene, info);
    }

    @Override
    public SoftCircleMedium makeVertexSet() {
        return new SoftCircleMedium();
    }
}
