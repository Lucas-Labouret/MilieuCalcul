package ui.mediumApps;

import cannings.Canning;
import cannings.VertexCanningCompleter;
import cannings.vertexCannings.RoundedCoordDichotomyVCanning;
import computingMedia.media.SoftSquareMedium;
import savefileManagers.SoftSquareManager;
import ui.utils.TBIntInput;

public class SoftSquareApp extends MediumApp {
    public Canning DEFAULT_CANNING() { return new VertexCanningCompleter(new RoundedCoordDichotomyVCanning(medium)); }

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
