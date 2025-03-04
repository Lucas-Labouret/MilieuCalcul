package local.ui.savefileManager;

import local.computingMedium.vertexSets.HardHexSet;
import local.ui.vertexSetScene.VertexSetScene;
import local.ui.view.InformationBar;

public class HardHexManager extends SavefileManager {
    public HardHexManager(VertexSetScene scene, InformationBar info) {
        super(scene, info);
    }

    @Override
    protected HardHexSet makeVertexSet() {
        return new HardHexSet();
    }
}
