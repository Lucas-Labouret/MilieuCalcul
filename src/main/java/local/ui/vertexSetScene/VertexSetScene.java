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
import local.ui.view.TBIntInput;

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
    TBIntInput fpoIterations;

    Button save;
    TextField saveName;


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

        fpoIterations = new TBIntInput("FPO Iterations", "1");

        miseEnBoite = DEFAULT_MEB();

        save = new Button("Save");
        saveName = new TextField();
        save.setOnAction(event -> {
            if (vertexSet != null) VertexSet.toFile(vertexSet, "save/" + saveName.getText() + ".vtxs");
        });
        botToolBar.getItems().addAll(save, saveName);

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

    protected void fpoIteration() {
        if (vertexSet != null)
            for (int i=0; i<fpoIterations.getValue(); i++)
                FPOUtils.fpoIteration(vertexSet);
        showVertexSet();
    }

    protected void showMeb() {
        HashMap<Vertex, Coord> miseEnBoiteResult = miseEnBoite.miseEnBoite(vertexSet);
        for (Vertex vertex : miseEnBoiteResult.keySet()) {
            vertex.setId(miseEnBoiteResult.get(vertex).toString());
        }
        showVertexSet();
    }

    public void setMeb(MiseEnBoite miseEnBoite) {
        this.miseEnBoite = miseEnBoite;
    }

    void showVertexSet() {
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
