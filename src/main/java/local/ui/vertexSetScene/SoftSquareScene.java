package local.ui.vertexSetScene;

import local.computingMedium.miseEnBoite.MiseEnBoite;
import local.computingMedium.miseEnBoite.RoundedCoordMeb;
import local.computingMedium.vertexSets.SoftSquareSet;
import local.ui.savefileManager.SavefileManager;
import local.ui.savefileManager.SoftSquareManager;
import local.ui.view.TBIntInput;

public class SoftSquareScene extends VertexSetScene {
    public MiseEnBoite DEFAULT_MEB() { return new RoundedCoordMeb(); }

    TBIntInput pointCountField;

    public SoftSquareScene() {
        pointCountField = new TBIntInput("Cout", "20");
        pointCountField.setPrefWidth(50);

        topToolBar.getItems().addAll(pointCountField, gen, tri, fpo, meb);
        setTop(topToolBar);

        savefileManager = new SoftSquareManager(this, savefileInfo);
    }

    public SoftSquareScene(MiseEnBoite miseEnBoite) {
        this();
        this.miseEnBoite = miseEnBoite;
    }

    protected void generate() {
        int pointCount = pointCountField.getValue();
        vertexSet = new SoftSquareSet(pointCount);
        showVertexSet();
    }
}
