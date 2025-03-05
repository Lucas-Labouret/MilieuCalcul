package local.ui.mediumScene;

import local.computingMedium.miseEnBoite.VertexMeB;
import local.computingMedium.miseEnBoite.RoundedCoordVMeb;
import local.computingMedium.media.SoftSquareMedium;
import local.ui.savefileManager.SoftSquareManager;
import local.ui.view.TBIntInput;

public class SoftSquareScene extends MediumScene {
    public VertexMeB DEFAULT_MEB() { return new RoundedCoordVMeb(); }

    TBIntInput pointCountField;

    public SoftSquareScene() {
        pointCountField = new TBIntInput("Cout", "20");
        pointCountField.setPrefWidth(50);

        topToolBar.getItems().addAll(pointCountField, gen, tri, fpo, meb);
        setTop(topToolBar);

        savefileManager = new SoftSquareManager(this, savefileInfo);
    }

    public SoftSquareScene(VertexMeB miseEnBoite) {
        this();
        this.miseEnBoite = miseEnBoite;
    }

    protected void generate() {
        int pointCount = pointCountField.getValue();
        medium = new SoftSquareMedium(pointCount);
        showVertexSet();
    }
}
