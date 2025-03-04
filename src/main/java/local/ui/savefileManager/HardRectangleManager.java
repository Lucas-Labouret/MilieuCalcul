package local.ui.savefileManager;

import local.computingMedium.vertexSets.HardRectangleSet;
import local.ui.vertexSetScene.VertexSetScene;
import local.ui.view.InformationBar;

public class HardRectangleManager extends SavefileManager {
    public HardRectangleManager(VertexSetScene scene, InformationBar info) {
        super(scene, info);
    }

    @Override
    public HardRectangleSet makeVertexSet() {
        return new HardRectangleSet();
    }
}
