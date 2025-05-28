package local.ui.mediumApps;

import local.computingMedia.cannings.Canning;
import local.computingMedia.cannings.VertexCanningCompleter;
import local.computingMedia.cannings.vertexCannings.RoundedCoordDichotomyVCanning;
import local.computingMedia.media.SoftSquareMedium;
import local.savefileManagers.SoftSquareManager;
import local.ui.utils.TBIntInput;

public class SoftSquareApp extends MediumApp {
    public Canning DEFAULT_CANNING() { return new VertexCanningCompleter(new RoundedCoordDichotomyVCanning()); }

    final TBIntInput pointCountField;

    public SoftSquareApp() {
        pointCountField = new TBIntInput("Count", "20");
        pointCountField.setPrefWidth(50);

        topToolBar.getItems().addAll(pointCountField, gen, tri, fpo, msk);
        setTop(topToolBar);

        savefileManager = new SoftSquareManager();
    }

    protected void generate() {
        int pointCount = pointCountField.getValue();
        medium = new SoftSquareMedium(pointCount);
        generateCommon();
    }
}
