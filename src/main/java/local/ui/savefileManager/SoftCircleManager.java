package local.ui.savefileManager;

import local.computingMedium.vertexSets.SoftCircleSet;
import local.ui.vertexSetScene.VertexSetScene;
import local.ui.view.InformationBar;

public class SoftCircleManager extends SavefileManager<SoftCircleSet> {
    public SoftCircleManager(VertexSetScene scene, InformationBar info) {
        super(scene, info);
    }

    @Override
    public SoftCircleSet makeVertexSet() {
        return new SoftCircleSet();
    }
}
