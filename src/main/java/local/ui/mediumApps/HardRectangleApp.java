package local.ui.mediumApps;

import local.computingMedia.cannings.VertexCanning;
import local.computingMedia.cannings.TopDistanceXSortedLinesVCanning;
import local.computingMedia.media.HardRectangleMedium;
import local.savefileManagers.HardRectangleManager;
import local.ui.utils.TBIntInput;

public class HardRectangleApp extends MediumApp {
    @Override public VertexCanning DEFAULT_CANNING() { return new TopDistanceXSortedLinesVCanning(); }

    private final TBIntInput ptCountInput;
    private final TBIntInput widthInput;

    private final double width;

    public HardRectangleApp(double width) {
        ptCountInput = new TBIntInput("Count", "16");
        widthInput = new TBIntInput("Width", "6");

        this.width = width;

        topToolBar.getItems().addAll(ptCountInput, widthInput, gen, tri, fpo, can);
        setTop(topToolBar);

        savefileManager = new HardRectangleManager();
    }

    @Override
    protected void generate() {
        int pointCount = this.ptCountInput.getValue();
        int vertexWidth = this.widthInput.getValue();
        medium = new HardRectangleMedium(width, vertexWidth, pointCount);
        canning.setMedium(medium);
        needRecanning = true;
        showVertexSet();
    }
}
