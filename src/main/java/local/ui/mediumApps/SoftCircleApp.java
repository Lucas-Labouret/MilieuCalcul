package local.ui.mediumApps;

import local.computingMedia.miseEnBoite.VertexMeB;
import local.computingMedia.miseEnBoite.RoundedCoordVMeb;
import local.computingMedia.media.SoftCircleMedium;
import local.savefileManagers.SoftCircleManager;
import local.ui.utils.TBIntInput;

public class SoftCircleApp extends MediumApp {
    public VertexMeB DEFAULT_MEB() { return new RoundedCoordVMeb(); }

    private final TBIntInput ptCountInput;

    public SoftCircleApp() {
        ptCountInput = new TBIntInput("Count", "20");

        topToolBar.getItems().addAll(ptCountInput, gen, tri, fpo, meb);
        setTop(topToolBar);

        savefileManager = new SoftCircleManager();
    }

    @Override
    protected void generate() {
        int pointCount = this.ptCountInput.getValue();
        medium = new SoftCircleMedium(pointCount);
        showVertexSet();
    }
}
