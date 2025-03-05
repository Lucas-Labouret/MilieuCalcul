package local.ui.mediumApps;

import local.computingMedia.miseEnBoite.VertexMeB;
import local.computingMedia.miseEnBoite.RoundedCoordVMeb;
import local.computingMedia.media.SoftSquareMedium;
import local.savefileManagers.SoftSquareManager;
import local.ui.utils.TBIntInput;

public class SoftSquareApp extends MediumApp {
    public VertexMeB DEFAULT_MEB() { return new RoundedCoordVMeb(); }

    final TBIntInput pointCountField;

    public SoftSquareApp() {
        pointCountField = new TBIntInput("Cout", "20");
        pointCountField.setPrefWidth(50);

        topToolBar.getItems().addAll(pointCountField, gen, tri, fpo, meb);
        setTop(topToolBar);

        savefileManager = new SoftSquareManager();
    }

    protected void generate() {
        int pointCount = pointCountField.getValue();
        medium = new SoftSquareMedium(pointCount);
        showVertexSet();
    }
}
