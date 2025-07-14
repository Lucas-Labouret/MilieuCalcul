package ui.mediumApps;

import cannings.Canning;
import cannings.VertexCanningCompleter;
import cannings.vertexCannings.RoundedCoordDichotomyVCanning;
import computingMedia.media.SoftCircleMedium;
import savefileManagers.SoftCircleManager;
import ui.utils.TBIntInput;

public class SoftCircleApp extends MediumApp {
    public Canning DEFAULT_CANNING() { return new VertexCanningCompleter(new RoundedCoordDichotomyVCanning(medium)); }

    private final TBIntInput ptCountInput;

    public SoftCircleApp() {
        ptCountInput = new TBIntInput("Count", "20");

        registerTopToolBarInput(ptCountInput);
        buildTopToolBar();

        savefileManager = new SoftCircleManager();
    }

    @Override
    protected void generate() {
        int pointCount = this.ptCountInput.getValue();
        medium = new SoftCircleMedium(pointCount);
        generateCommon();
    }
}
