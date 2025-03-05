package local.ui.mediumScene;

import local.computingMedium.miseEnBoite.VertexMeB;
import local.computingMedium.miseEnBoite.RoundedCoordVMeb;
import local.computingMedium.media.SoftCircleMedium;
import local.ui.savefileManager.SoftCircleManager;
import local.ui.view.TBIntInput;

public class SoftCircleScene extends MediumScene {
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
        medium = new SoftCircleMedium(pointCount);
        showVertexSet();
    }
}
