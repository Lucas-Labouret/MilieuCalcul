package local.ui.vertexSetScene;

import local.computingMedium.miseEnBoite.MiseEnBoite;
import local.computingMedium.miseEnBoite.RoundedCoordMeb;
import local.computingMedium.vertexSets.SoftCircleSet;
import local.ui.savefileManager.SavefileManager;
import local.ui.savefileManager.SoftCircleManager;
import local.ui.view.TBIntInput;

public class SoftCircleScene extends VertexSetScene {
    public MiseEnBoite DEFAULT_MEB() { return new RoundedCoordMeb(); }

    private final TBIntInput ptCountInput;

    public SoftCircleScene() {
        ptCountInput = new TBIntInput("Count", "20");

        topToolBar.getItems().addAll(ptCountInput, gen, tri, fpo, meb);
        setTop(topToolBar);

        savefileManager = new SoftCircleManager(this, savefileInfo);
    }

    public SoftCircleScene(MiseEnBoite miseEnBoite) {
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
