package local.ui.mediumApps;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import local.computingMedia.cannings.VertexCanning;
import local.computingMedia.media.Medium;
import local.ui.utils.InformationBar;
import local.ui.utils.MediumDrawer;
import local.savefileManagers.SavefileManager;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class MediumApp extends BorderPane {
    public abstract VertexCanning DEFAULT_CANNING();

    protected final ToolBar topToolBar;
    protected final ToolBar botToolBar;
    protected final InformationBar infoBar;
    protected final MediumDrawer drawPane;

    protected Medium medium;
    protected VertexCanning canning;

    protected final Button gen;
    protected final Button tri;
    protected final Button fpo;
    protected final Button can;

    protected final InformationBar savefileInfo;
    protected SavefileManager savefileManager;
    protected final TextField fileName;
    protected final Button saveButton;
    protected final Button loadButton;

    protected boolean fpoToConvergence = false;
    protected int fpoIterations = 1;
    protected double convergenceTolerance = 0.9;

    public MediumApp() {
        topToolBar = new ToolBar();
        botToolBar = new ToolBar();
        infoBar = new InformationBar("Information");
        drawPane = new MediumDrawer();

        setTop(topToolBar);
        setCenter(drawPane);
        setBottom(botToolBar);

        gen = new Button("Generate");
        gen.setOnAction((event) -> generate());
        tri = new Button("Triangulate");
        tri.setOnAction(event -> triangulate());
        fpo = new Button("FPO");
        fpo.setOnAction(event -> fpoIteration());
        can = new Button("Can");
        can.setOnAction(event -> this.can());

        canning = DEFAULT_CANNING();

        savefileInfo = new InformationBar();
        fileName = new TextField();
        saveButton = new Button("Save");
        saveButton.setOnAction(event -> save());
        loadButton = new Button("Load");
        loadButton.setOnAction(event -> load());
        botToolBar.getItems().addAll(fileName, saveButton, loadButton, savefileInfo);

        widthProperty().addListener((obs, oldVal, newVal) -> updateDrawPaneSize());
        heightProperty().addListener((obs, oldVal, newVal) -> updateDrawPaneSize());
        updateDrawPaneSize();
    }

    abstract protected void generate();

    protected void triangulate() {
        if (medium != null) {
            medium.delaunayTriangulate();
        }
        showVertexSet();
    }

    public void setFpoToConvergence(boolean fpoToConvergence) {
        this.fpoToConvergence = fpoToConvergence;
    }
    public void setFpoIterations(int fpoIterations) {
        this.fpoIterations = fpoIterations;
    }
    public void setConvergenceTolerance(double convergenceTolerance) {
        this.convergenceTolerance = convergenceTolerance;
    }

    protected void fpoIteration() {
        if (medium == null) return;
        needRecanning = true;

        if (fpoToConvergence) medium.optimizeToConvergence(convergenceTolerance);
        else medium.optimizeToSetIterations(fpoIterations);
        showVertexSet();
    }

    private void save(){
        if (fileName.getText().isEmpty()){
            savefileInfo.setText("Please enter a file name");
            return;
        }
        try { savefileManager.save(medium, getFileName()); }
        catch (IOException e) {
            savefileInfo.setText("Failed to save to a file.");
            e.printStackTrace();
        }
    }
    private void load(){
        try {
            medium = savefileManager.load(getFileName());
            canning.setMedium(medium);
            needRecanning = true;
        }
        catch (FileNotFoundException e) { savefileInfo.setText("File not found."); }
        catch (IOException e) {
            savefileInfo.setText("File exists but couldn't be read.");
            e.printStackTrace();
        }
        showVertexSet();
    }

    protected boolean needRecanning = true;
    protected void can() {
        if (medium == null || !needRecanning) return;
        canning.can();
        needRecanning = false;
        showVertexSet();
    }

    public void setCanning(VertexCanning canning) {
        this.canning = canning;
        this.canning.setMedium(medium);
        needRecanning = true;
    }

    public String getFileName() { return fileName.getText(); }

    public void showVertexSet() {
        if (needRecanning) drawPane.showMedium(medium, null);
        else drawPane.showMedium(medium, canning.getVertexCanning());
    }

    private void updateDrawPaneSize() {
        drawPane.setPrefWidth(getWidth());
        drawPane.setPrefHeight(getHeight() - topToolBar.getHeight() - botToolBar.getHeight());
        if (medium != null) {
            showVertexSet();
        }
    }
}
