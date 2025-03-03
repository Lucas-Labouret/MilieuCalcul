package local.ui.vertexSetScene;

import local.computingMedium.miseEnBoite.VertexMeB;
import local.computingMedium.miseEnBoite.RoundedCoordVMeb;
import local.computingMedium.vertexSets.SoftCircleSet;
import local.ui.savefileManager.SoftCircleManager;
import local.ui.view.TBIntInput;

public class SoftCircleScene extends VertexSetScene {
    public VertexMeB DEFAULT_MEB() { return new RoundedCoordVMeb(); }

    private final TBIntInput ptCountInput;

    public SoftCircleScene() {
        ptCountInput = new TBIntInput("Count", "20");

        topToolBar.getItems().addAll(ptCountInput, gen, tri, fpo, meb);
        setTop(topToolBar);

        savefileManager = new SoftCircleManager(this, savefileInfo);
    }

    public SoftCircleScene(VertexMeB miseEnBoite) {
        this();
        this.miseEnBoite = miseEnBoite;
    }

    @Override
    protected void generate() {
        int pointCount = this.ptCountInput.getValue();
        vertexSet = new SoftCircleSet(pointCount);
        showVertexSet();
    }
}
