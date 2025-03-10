package local.ui.mediumApps;

import local.computingMedia.cannings.VertexCanning;
import local.computingMedia.cannings.RoundedCoordVCanning;
import local.computingMedia.media.SoftSquareMedium;
import local.savefileManagers.SoftSquareManager;
import local.ui.utils.TBIntInput;

public class SoftSquareApp extends MediumApp {
    public VertexCanning DEFAULT_CANNING() { return new RoundedCoordVCanning(); }

    final TBIntInput pointCountField;

    public SoftSquareApp() {
        pointCountField = new TBIntInput("Count", "20");
        pointCountField.setPrefWidth(50);

        topToolBar.getItems().addAll(pointCountField, gen, tri, fpo, can);
        setTop(topToolBar);

        savefileManager = new SoftSquareManager();
    }

    protected void generate() {
        int pointCount = pointCountField.getValue();
        medium = new SoftSquareMedium(pointCount);
        canning.setMedium(medium);
        needRecanning = true;
        showVertexSet();
    }
}
