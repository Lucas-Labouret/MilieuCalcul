package local.ui.mediumScene;

import local.computingMedium.miseEnBoite.VertexMeB;
import local.computingMedium.miseEnBoite.TopDistanceXSortedLinesVMeb;
import local.computingMedium.media.HardHexMedium;
import local.ui.savefileManager.HardHexManager;
import local.ui.view.TBIntInput;

public class HardHexScene extends MediumScene {
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
        medium = new HardHexMedium(width, height, pointCount);
        showVertexSet();
    }
}

