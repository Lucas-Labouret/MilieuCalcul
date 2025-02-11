package local.ui.vertexSetScene;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import local.furthestpointoptimization.model.miseEnBoite.Coord;
import local.furthestpointoptimization.model.miseEnBoite.MiseEnBoite;
import local.furthestpointoptimization.model.optimisation.FPOUtils;
import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.vertexSets.VertexSet;
import local.ui.view.InformationBar;
import local.ui.view.PaneVertexSetDrawer;

import java.util.HashMap;

public abstract class VertexSetScene extends BorderPane {
    ToolBar toolbar;
    InformationBar infoBar;
    PaneVertexSetDrawer drawPane;

    VertexSet vertexSet;
    MiseEnBoite miseEnBoite;

    Button gen, tri, fpo, meb;

    VertexSetScene(MiseEnBoite miseEnBoite) {
        toolbar = new ToolBar();
        infoBar = new InformationBar("Information");
        drawPane = new PaneVertexSetDrawer(infoBar, null);

        setTop(toolbar);
        setCenter(drawPane);
        setBottom(infoBar);

        this.miseEnBoite = miseEnBoite;

        gen = new Button("Generate");
        gen.setOnAction((event) -> generate());
        tri = new Button("Triangulate");
        tri.setOnAction(event -> triangulate());
        fpo = new Button("FPO");
        fpo.setOnAction(event -> fpoIteration());
        meb = new Button("Met en boite");
        meb.setOnAction(e -> this.showMeb());

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
        if (vertexSet != null) {
            for (int i=0; i<1; i++) FPOUtils.fpoIteration(vertexSet);
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

    public void setMeb(MiseEnBoite miseEnBoite) {
        this.miseEnBoite = miseEnBoite;
    }

    void showVertexSet() {
        drawPane.showVertexSet(vertexSet);
    }

    private void updateDrawPaneSize() {
        drawPane.setPrefWidth(getWidth());
        drawPane.setPrefHeight(getHeight() - toolbar.getHeight() - infoBar.getHeight());
        if (vertexSet != null) {
            showVertexSet();
        }
    }
}
