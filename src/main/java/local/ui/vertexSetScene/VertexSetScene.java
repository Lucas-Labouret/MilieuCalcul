package local.ui.vertexSetScene;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import local.computingMedium.Vertex;
import local.computingMedium.miseEnBoite.MiseEnBoite;
import local.misc.Coord;
import local.computingMedium.vertexSets.VertexSet;
import local.furthestpointoptimization.FPOUtils;
import local.ui.view.InformationBar;
import local.ui.view.PaneVertexSetDrawer;
import local.ui.view.SavefileManager;

import java.util.HashMap;

public abstract class VertexSetScene extends BorderPane {
    public abstract MiseEnBoite DEFAULT_MEB();

    ToolBar topToolBar;
    ToolBar botToolBar;
    InformationBar infoBar;
    PaneVertexSetDrawer drawPane;

    VertexSet vertexSet;
    MiseEnBoite miseEnBoite;

    Button gen, tri, fpo, meb;

    InformationBar savefileInfo;
    SavefileManager savefileManager;
    TextField fileName;
    Button saveButton, loadButton;

    boolean fpoToConvergence = false;
    int fpoIterations = 1;
    double convergenceTolerance = 0.9;

    public VertexSetScene() {
        topToolBar = new ToolBar();
        botToolBar = new ToolBar();
        infoBar = new InformationBar("Information");
        drawPane = new PaneVertexSetDrawer(infoBar, null);

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
        savefileManager = new SavefileManager(this, savefileInfo);
        fileName = new TextField();
        saveButton = new Button("Save");
        saveButton.setOnAction(event -> savefileManager.save());
        loadButton = new Button("Load");
        loadButton.setOnAction(event -> savefileManager.load());
        botToolBar.getItems().addAll(fileName, saveButton, loadButton, savefileInfo);

        widthProperty().addListener((obs, oldVal, newVal) -> updateDrawPaneSize());
        heightProperty().addListener((obs, oldVal, newVal) -> updateDrawPaneSize());
        updateDrawPaneSize();
    }

    abstract protected void generate();

    protected void triangulate() {
        if (vertexSet != null) {
            vertexSet.delaunayTriangulate();
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
        if (vertexSet == null) return;

        if (fpoToConvergence) {
            FPOUtils.buildFPO(vertexSet, convergenceTolerance);
        } else {
            for (int i=0; i<fpoIterations; i++) FPOUtils.fpoIteration(vertexSet);
        }
        showVertexSet();
    }

    protected void showMeb() {
        HashMap<Vertex, Coord> miseEnBoiteResult = miseEnBoite.miseEnBoite(vertexSet);
        for (Vertex vertex : miseEnBoiteResult.keySet()) {
            vertex.setId(miseEnBoiteResult.get(vertex).toString());
        }
        showVertexSet();
    }

    public void setMeb(MiseEnBoite miseEnBoite) { this.miseEnBoite = miseEnBoite; }

    public void setVertexSet(VertexSet vertexSet) { this.vertexSet = vertexSet; }
    public VertexSet getVertexSet() { return vertexSet; }

    public String getFileName() { return fileName.getText(); }

    public void showVertexSet() {
        drawPane.showVertexSet(vertexSet);
    }

    private void updateDrawPaneSize() {
        drawPane.setPrefWidth(getWidth());
        drawPane.setPrefHeight(getHeight() - topToolBar.getHeight() - botToolBar.getHeight());
        if (vertexSet != null) {
            showVertexSet();
        }
    }
}
