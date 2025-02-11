package local.ui.vertexSetScene;

import local.furthestpointoptimization.model.miseEnBoite.MiseEnBoite;
import local.furthestpointoptimization.model.miseEnBoite.RoundedCoordMeb;
import local.furthestpointoptimization.model.vertexSets.SoftCircleSet;
import local.ui.view.TBIntInput;

public class SoftCircleScene extends VertexSetScene {
    public MiseEnBoite DEFAULT_MEB() { return new RoundedCoordMeb(); }

    private final TBIntInput ptCountInput;

    public SoftCircleScene() {
        ptCountInput = new TBIntInput("Count", "20");

        toolbar.getItems().addAll(ptCountInput, gen, tri, fpo, fpoIterations, meb);
        setTop(toolbar);
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
