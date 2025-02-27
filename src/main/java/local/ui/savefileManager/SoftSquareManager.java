package local.ui.savefileManager;

import local.computingMedium.vertexSets.SoftSquareSet;
import local.ui.vertexSetScene.VertexSetScene;
import local.ui.view.InformationBar;

public class SoftSquareManager extends SavefileManager<SoftSquareSet> {
    public SoftSquareManager(VertexSetScene scene, InformationBar info) {
        super(scene, info);
    }

    @Override
    public SoftSquareSet makeVertexSet() {
        return new SoftSquareSet();
    }
}
