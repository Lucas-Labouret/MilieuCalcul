package local.ui.mediumApps;

import local.computingMedia.miseEnBoite.VertexMeB;
import local.computingMedia.miseEnBoite.TopDistanceXSortedLinesVMeb;
import local.computingMedia.media.HardRectangleMedium;
import local.savefileManagers.HardRectangleManager;
import local.ui.utils.TBIntInput;

public class HardRectangleApp extends MediumApp {
    @Override public VertexMeB DEFAULT_MEB() { return new TopDistanceXSortedLinesVMeb(); }

    private final TBIntInput ptCountInput;
    private final TBIntInput widthInput;

    private final double width;

    public HardRectangleApp(double width) {
        ptCountInput = new TBIntInput("Count", "16");
        widthInput = new TBIntInput("Width", "6");

        this.width = width;

        topToolBar.getItems().addAll(ptCountInput, widthInput, gen, tri, fpo, meb);
        setTop(topToolBar);

        savefileManager = new HardRectangleManager();
    }

    @Override
    protected void generate() {
        int pointCount = this.ptCountInput.getValue();
        int vertexWidth = this.widthInput.getValue();
        medium = new HardRectangleMedium(width, vertexWidth, pointCount);
        showVertexSet();
    }
}
