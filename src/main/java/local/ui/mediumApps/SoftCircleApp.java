package local.ui.mediumApps;

import local.computingMedia.cannings.Canning;
import local.computingMedia.cannings.VertexCanningCompleter;
import local.computingMedia.cannings.vertexCannings.RoundedCoordVCanning;
import local.computingMedia.media.SoftCircleMedium;
import local.savefileManagers.SoftCircleManager;
import local.ui.utils.TBIntInput;

public class SoftCircleApp extends MediumApp {
    public Canning DEFAULT_CANNING() { return new VertexCanningCompleter(new RoundedCoordVCanning()); }

    private final TBIntInput ptCountInput;

    public SoftCircleApp() {
        ptCountInput = new TBIntInput("Count", "20");

        topToolBar.getItems().addAll(ptCountInput, gen, tri, fpo);
        setTop(topToolBar);

        savefileManager = new SoftCircleManager();
    }

    @Override
    protected void generate() {
        int pointCount = this.ptCountInput.getValue();
        medium = new SoftCircleMedium(pointCount);
        generateCommon();
    }
}
