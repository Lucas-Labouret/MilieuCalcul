package local.ui.vertexSetScene;

import local.computingMedium.miseEnBoite.MiseEnBoite;
import local.computingMedium.miseEnBoite.TopDistanceXSortedLinesMeb;
import local.computingMedium.vertexSets.HardSquareSet;
import local.ui.view.TBIntInput;

public class HardSquareScene extends VertexSetScene {
    @Override public MiseEnBoite DEFAULT_MEB() { return new TopDistanceXSortedLinesMeb(); }

    private final TBIntInput ptCountInput;
    private final TBIntInput widthInput;

    public HardSquareScene() {
        ptCountInput = new TBIntInput("Count", "16");
        widthInput = new TBIntInput("Width", "6");

        toolbar.getItems().addAll(ptCountInput, widthInput, gen, tri, fpo, fpoIterations, meb);
        setTop(toolbar);
    }

    public HardSquareScene(MiseEnBoite miseEnBoite) {
        this();
        this.miseEnBoite = miseEnBoite;
    }

    @Override
    protected void generate() {
        int pointCount = this.ptCountInput.getValue();
        int width = this.widthInput.getValue();
        vertexSet = new HardSquareSet(width, pointCount);
        showVertexSet();
    }
}
