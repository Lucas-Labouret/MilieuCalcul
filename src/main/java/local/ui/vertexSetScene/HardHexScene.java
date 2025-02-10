package local.ui.vertexSetScene;

import java.util.HashMap;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import local.ui.view.InformationBar;
import local.ui.view.PaneVertexSetDrawer;
import local.ui.view.TBIntInput;
import local.furthestpointoptimization.model.miseEnBoite.Coord;
import local.furthestpointoptimization.model.miseEnBoite.MiseEnBoite;
import local.furthestpointoptimization.model.miseEnBoite.TopDistanceXSortedLinesMeb;
import local.furthestpointoptimization.model.optimisation.FPOUtils;
import local.furthestpointoptimization.model.vertexSets.Vertex;
import local.furthestpointoptimization.model.vertexSets.VertexSet;

public class HardHexScene extends BorderPane {

    ToolBar toolbar;
    private final TBIntInput ptCountInput;
    private final TBIntInput heightInput;
    InformationBar infoBar;
    PaneVertexSetDrawer drawPane;
    VertexSet vertexSet;

    Vertex selection;

    public HardHexScene() {
        toolbar = new ToolBar();
        drawPane = new PaneVertexSetDrawer(infoBar, selection);
        infoBar = new InformationBar("Information");

        setTop(toolbar);
        setCenter(drawPane);
        setBottom(infoBar);

        ptCountInput = new TBIntInput("Count", "20");
        heightInput = new TBIntInput("Height", "7");

        Button gen = new Button("Generate");
        gen.setOnAction((event) -> this.generate());
        Button tri = new Button("Triangulate");
        tri.setOnAction((event) -> this.triangulate());
        Button fpo = new Button("FPO");
        fpo.setOnAction(e -> this.fpoIteration());
        Button MEB = new Button("Met en boite");
        MEB.setOnAction(e -> this.showMEB());

        toolbar.getItems().addAll(ptCountInput, heightInput, gen, tri, fpo, MEB);
        setTop(toolbar);

        // drawPane = new Pane();
        setCenter(drawPane);

        widthProperty().addListener((obs, oldVal, newVal) -> updateDrawPaneSize());
        heightProperty().addListener((obs, oldVal, newVal) -> updateDrawPaneSize());
        updateDrawPaneSize();
    }

    void showVertexSet() {
        drawPane.showVertexSet(vertexSet);
    }

    private void generate() {
        int pointCount = this.ptCountInput.getValue();
        int height = this.heightInput.getValue();
        int width = (int) (height * Math.sqrt(2));
        vertexSet = VertexSet.newHexBorderedSet(width, height, pointCount);
        showVertexSet();
    }

    private void triangulate() {
        if (vertexSet != null) {
            vertexSet.delaunayTriangulate();
            showVertexSet();
        }
    }

    private void fpoIteration() {
        if (vertexSet != null) {
            FPOUtils.fpoIteration(vertexSet);
        }
        showVertexSet();
    }

    private void showMEB() {
        MiseEnBoite miseEnBoite = new TopDistanceXSortedLinesMeb();
        HashMap<Vertex, Coord> miseEnBoiteResult = miseEnBoite.miseEnBoite(vertexSet);
        for (Vertex vertex : miseEnBoiteResult.keySet()) {
            vertex.setId(miseEnBoiteResult.get(vertex).toString());
        }
        showVertexSet();
    }

    private void updateDrawPaneSize() {
        drawPane.setPrefWidth(getWidth());
        drawPane.setPrefHeight(getHeight() - toolbar.getHeight() - infoBar.getHeight());
        if (vertexSet != null) {
            showVertexSet();
        }
    }
}
