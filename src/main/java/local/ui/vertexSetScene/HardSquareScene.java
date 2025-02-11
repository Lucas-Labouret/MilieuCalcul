package local.ui.vertexSetScene;

import local.furthestpointoptimization.model.miseEnBoite.MiseEnBoite;
import local.furthestpointoptimization.model.vertexSets.HardSquareSet;
import local.ui.view.TBIntInput;

public class HardSquareScene extends VertexSetScene {
    private final TBIntInput ptCountInput;
    private final TBIntInput widthInput;

    public HardSquareScene(MiseEnBoite miseEnBoite) {
        super(miseEnBoite);

        ptCountInput = new TBIntInput("Count", "16");
        widthInput = new TBIntInput("Width", "6");

        toolbar.getItems().addAll(ptCountInput, widthInput, gen, tri, fpo, meb);
        setTop(toolbar);
    }

    @Override
    protected void generate() {
        int pointCount = this.ptCountInput.getValue();
        int width = this.widthInput.getValue();
        vertexSet = new HardSquareSet(width, pointCount);
        showVertexSet();
    }
}
