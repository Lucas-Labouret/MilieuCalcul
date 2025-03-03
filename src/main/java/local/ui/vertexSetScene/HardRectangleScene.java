package local.ui.vertexSetScene;

import local.computingMedium.miseEnBoite.VertexMeB;
import local.computingMedium.miseEnBoite.TopDistanceXSortedLinesVMeb;
import local.computingMedium.vertexSets.HardRectangleSet;
import local.ui.savefileManager.HardRectangleManager;
import local.ui.view.TBIntInput;

public class HardRectangleScene extends VertexSetScene {
    @Override public VertexMeB DEFAULT_MEB() { return new TopDistanceXSortedLinesVMeb(); }

    private final TBIntInput ptCountInput;
    private final TBIntInput widthInput;

    private double width;

    public HardRectangleScene(double width) {
        ptCountInput = new TBIntInput("Count", "16");
        widthInput = new TBIntInput("Width", "6");

        this.width = width;

        topToolBar.getItems().addAll(ptCountInput, widthInput, gen, tri, fpo, meb);
        setTop(topToolBar);

        savefileManager = new HardRectangleManager(this, savefileInfo);
    }

    public HardRectangleScene(double width, VertexMeB miseEnBoite) {
        this(width);
        this.miseEnBoite = miseEnBoite;
    }

    @Override
    protected void generate() {
        int pointCount = this.ptCountInput.getValue();
        int vertexWidth = this.widthInput.getValue();
        vertexSet = new HardRectangleSet(width, vertexWidth, pointCount);
        showVertexSet();
    }
}
