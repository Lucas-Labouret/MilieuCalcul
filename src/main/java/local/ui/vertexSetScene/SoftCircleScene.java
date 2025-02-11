package local.ui.vertexSetScene;

import local.furthestpointoptimization.model.miseEnBoite.MiseEnBoite;
import local.furthestpointoptimization.model.vertexSets.SoftCircleSet;
import local.ui.view.TBIntInput;

public class SoftCircleScene extends VertexSetScene {
    private final TBIntInput ptCountInput;

    public SoftCircleScene(MiseEnBoite miseEnBoite) {
        super(miseEnBoite);

        ptCountInput = new TBIntInput("Count", "20");

        toolbar.getItems().addAll(ptCountInput, gen, tri, fpo, meb);
        setTop(toolbar);
    }

    @Override
    protected void generate() {
        int pointCount = this.ptCountInput.getValue();
        vertexSet = new SoftCircleSet(pointCount);
        showVertexSet();
    }
}
