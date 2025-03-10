package local.ui.mediumApps;

import local.computingMedia.cannings.VertexCanning;
import local.computingMedia.cannings.RoundedCoordVCanning;
import local.computingMedia.media.SoftCircleMedium;
import local.savefileManagers.SoftCircleManager;
import local.ui.utils.TBIntInput;

public class SoftCircleApp extends MediumApp {
    public VertexCanning DEFAULT_CANNING() { return new RoundedCoordVCanning(); }

    private final TBIntInput ptCountInput;

    public SoftCircleApp() {
        ptCountInput = new TBIntInput("Count", "20");

        topToolBar.getItems().addAll(ptCountInput, gen, tri, fpo, can);
        setTop(topToolBar);

        savefileManager = new SoftCircleManager();
    }

    @Override
    protected void generate() {
        int pointCount = this.ptCountInput.getValue();
        medium = new SoftCircleMedium(pointCount);
        canning.setMedium(medium);
        needRecanning = true;
        showVertexSet();
    }
}
