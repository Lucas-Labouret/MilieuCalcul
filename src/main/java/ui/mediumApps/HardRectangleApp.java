package ui.mediumApps;

import cannings.Canning;
import cannings.VertexCanningCompleter;
import cannings.vertexCannings.RoundedCoordIncrementalVCanning;
import computingMedia.media.HardRectangleMedium;
import savefileManagers.HardRectangleManager;
import ui.utils.TBIntInput;

public class HardRectangleApp extends MediumApp {
    @Override public Canning DEFAULT_CANNING() { return new VertexCanningCompleter(new RoundedCoordIncrementalVCanning(medium)); }

    private final TBIntInput ptCountInput;
    private final TBIntInput widthInput;

    private final double width;

    public HardRectangleApp(double width) {
        ptCountInput = new TBIntInput("Count", "16");
        widthInput = new TBIntInput("Width", "6");

        this.width = width;

        topToolBar.getItems().addAll(ptCountInput, widthInput, gen, tri, fpo, msk);
        setTop(topToolBar);

        savefileManager = new HardRectangleManager();
    }

    @Override
    protected void generate() {
        int pointCount = this.ptCountInput.getValue();
        int vertexWidth = this.widthInput.getValue();
        medium = new HardRectangleMedium(width, vertexWidth, pointCount);
        generateCommon();
    }
}
