package local.ui.vertexSetScene;

import local.computingMedium.miseEnBoite.VertexMeB;
import local.computingMedium.miseEnBoite.TopDistanceXSortedLinesVMeb;
import local.computingMedium.vertexSets.HardHexSet;
import local.ui.savefileManager.HardHexManager;
import local.ui.view.TBIntInput;

public class HardHexScene extends VertexSetScene {
    @Override public VertexMeB DEFAULT_MEB() { return new TopDistanceXSortedLinesVMeb(); }

    private final TBIntInput ptCountInput;
    private final TBIntInput heightInput;

    public HardHexScene() {
        ptCountInput = new TBIntInput("Count", "20");
        heightInput = new TBIntInput("Height", "7");

        topToolBar.getItems().addAll(ptCountInput, heightInput, gen, tri, fpo, meb);
        setTop(topToolBar);

        savefileManager = new HardHexManager(this, savefileInfo);
    }

    public HardHexScene(VertexMeB miseEnBoite) {
        this();
        this.miseEnBoite = miseEnBoite;
    }

    protected void generate() {
        int pointCount = this.ptCountInput.getValue();
        int height = this.heightInput.getValue();
        int width = (int) (height * Math.sqrt(2));
        vertexSet = new HardHexSet(width, height, pointCount);
        showVertexSet();
    }
}

