package local.ui.savefileManager;

import local.computingMedium.media.HardHexMedium;
import local.ui.mediumScene.MediumScene;
import local.ui.view.InformationBar;

public class HardHexManager extends SavefileManager {
    public HardHexManager(MediumScene scene, InformationBar info) {
        super(scene, info);
    }

    @Override
    protected HardHexMedium makeVertexSet() {
        return new HardHexMedium();
    }
}
