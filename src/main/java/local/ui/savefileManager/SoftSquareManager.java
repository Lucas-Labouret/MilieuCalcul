package local.ui.savefileManager;

import local.computingMedium.media.SoftSquareMedium;
import local.ui.mediumScene.MediumScene;
import local.ui.view.InformationBar;

public class SoftSquareManager extends SavefileManager {
    public SoftSquareManager(MediumScene scene, InformationBar info) {
        super(scene, info);
    }

    @Override
    public SoftSquareMedium makeVertexSet() {
        return new SoftSquareMedium();
    }
}
