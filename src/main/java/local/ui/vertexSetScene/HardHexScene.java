package local.ui.vertexSetScene;

import local.computingMedium.miseEnBoite.MiseEnBoite;
import local.computingMedium.miseEnBoite.TopDistanceXSortedLinesMeb;
import local.computingMedium.vertexSets.HardHexSet;
import local.ui.savefileManager.HardHexManager;
import local.ui.savefileManager.SavefileManager;
import local.ui.view.TBIntInput;

public class HardHexScene extends VertexSetScene {
    @Override public MiseEnBoite DEFAULT_MEB() { return new TopDistanceXSortedLinesMeb(); }

    private final TBIntInput ptCountInput;
    private final TBIntInput heightInput;

    public HardHexScene() {
        ptCountInput = new TBIntInput("Count", "20");
        heightInput = new TBIntInput("Height", "7");

        topToolBar.getItems().addAll(ptCountInput, heightInput, gen, tri, fpo, meb);
        setTop(topToolBar);

        savefileManager = new HardHexManager(this, savefileInfo);
    }

    public HardHexScene(MiseEnBoite miseEnBoite) {
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

