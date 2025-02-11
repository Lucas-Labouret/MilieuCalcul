package local.ui.vertexSetScene;

import local.furthestpointoptimization.model.miseEnBoite.MiseEnBoite;
import local.furthestpointoptimization.model.vertexSets.HardHexSet;
import local.ui.view.TBIntInput;
import local.furthestpointoptimization.model.vertexSets.VertexSet;

public class HardHexScene extends VertexSetScene {
    private final TBIntInput ptCountInput;
    private final TBIntInput heightInput;

    public HardHexScene(MiseEnBoite miseEnBoite) {
        super(miseEnBoite);

        ptCountInput = new TBIntInput("Count", "20");
        heightInput = new TBIntInput("Height", "7");

        toolbar.getItems().addAll(ptCountInput, heightInput, gen, tri, fpo, meb);
        setTop(toolbar);
    }

    void showVertexSet() {
        drawPane.showVertexSet(vertexSet);
    }

    protected void generate() {
        int pointCount = this.ptCountInput.getValue();
        int height = this.heightInput.getValue();
        int width = (int) (height * Math.sqrt(2));
        vertexSet = new HardHexSet(width, height, pointCount);
        showVertexSet();
    }
}

