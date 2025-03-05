package local.ui.mediumScene;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import local.computingMedium.Vertex;
import local.computingMedium.miseEnBoite.VertexMeB;
import local.computingMedium.miseEnBoite.Coord;
import local.computingMedium.media.Medium;
import local.furthestpointoptimization.FPOUtils;
import local.ui.view.InformationBar;
import local.ui.view.PaneMediumDrawer;
import local.ui.savefileManager.SavefileManager;

import java.util.HashMap;

public abstract class MediumScene extends BorderPane {
    public abstract VertexMeB DEFAULT_MEB();

    ToolBar topToolBar;
    ToolBar botToolBar;
    InformationBar infoBar;
    PaneMediumDrawer drawPane;

    Medium medium;
    VertexMeB miseEnBoite;

    Button gen, tri, fpo, meb;

    InformationBar savefileInfo;
    SavefileManager savefileManager;
    TextField fileName;
    Button saveButton, loadButton;

    boolean fpoToConvergence = false;
    int fpoIterations = 1;
    double convergenceTolerance = 0.9;

    public MediumScene() {
        topToolBar = new ToolBar();
        botToolBar = new ToolBar();
        infoBar = new InformationBar("Information");
        drawPane = new PaneMediumDrawer(infoBar, null);

        setTop(topToolBar);
        setCenter(drawPane);
        setBottom(botToolBar);

        gen = new Button("Generate");
        gen.setOnAction((event) -> generate());
        tri = new Button("Triangulate");
        tri.setOnAction(event -> triangulate());
        fpo = new Button("FPO");
        fpo.setOnAction(event -> fpoIteration());
        meb = new Button("Met en boite");
        meb.setOnAction(event -> this.showMeb());

        miseEnBoite = DEFAULT_MEB();

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

        if (fpoToConvergence) {
            FPOUtils.buildFPO(medium, convergenceTolerance);
        } else {
            for (int i=0; i<fpoIterations; i++) FPOUtils.fpoIteration(medium);
        }
        showVertexSet();
    }

    private void save(){
        savefileManager.save();
    }
    private void load(){
        savefileManager.load();
        showVertexSet();
    }

    protected void showMeb() {
        HashMap<Vertex, Coord> miseEnBoiteResult = miseEnBoite.miseEnBoite(medium);
        for (Vertex vertex : miseEnBoiteResult.keySet()) {
            vertex.setId(miseEnBoiteResult.get(vertex).toString());
        }
        showVertexSet();
    }

    public void setMeb(VertexMeB miseEnBoite) { this.miseEnBoite = miseEnBoite; }

    public void setVertexSet(Medium medium) { this.medium = medium; }
    public Medium getVertexSet() { return medium; }

    public String getFileName() { return fileName.getText(); }

    public void showVertexSet() {
        drawPane.showMedium(medium);
    }

    private void updateDrawPaneSize() {
        drawPane.setPrefWidth(getWidth());
        drawPane.setPrefHeight(getHeight() - topToolBar.getHeight() - botToolBar.getHeight());
        if (medium != null) {
            showVertexSet();
        }
    }
}
