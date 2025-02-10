package local.ui.vertexSetScene;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import local.furthestpointoptimization.model.miseEnBoite.Coord;
import local.furthestpointoptimization.model.miseEnBoite.MiseEnBoite;
import local.furthestpointoptimization.model.miseEnBoite.RoundedCoordMeb;
import local.furthestpointoptimization.model.vertexSets.Vertex;
import local.ui.view.InformationBar;
import local.ui.view.PaneVertexSetDrawer;
import local.furthestpointoptimization.model.vertexSets.VertexSet;
import local.furthestpointoptimization.model.optimisation.FPOUtils;

import java.util.HashMap;

public class SoftSquareScene extends BorderPane {
    ToolBar toolbar;
    InformationBar infoBar;
    PaneVertexSetDrawer drawPane;

    TextField pointCountField;

    private VertexSet vertexSet;

    public SoftSquareScene() {
        toolbar = new ToolBar();
        infoBar = new InformationBar("Information");
        drawPane = new PaneVertexSetDrawer(infoBar, null);

        setTop(toolbar);
        setCenter(drawPane);
        setBottom(infoBar);

        pointCountField = new TextField("20");
        pointCountField.setMaxWidth(40);

        // Generate button
        Button gen = new Button("Generate");
        gen.setOnAction((event) -> generate());
        Button triangulateButton = new Button("Triangulate");
        triangulateButton.setOnAction(event -> triangulate());
        Button fpo = new Button("FPO");
        fpo.setOnAction(event -> fpoIteration());
        Button meb = new Button("Met en boite");
        meb.setOnAction(e -> this.showMEB());

        toolbar.getItems().addAll(pointCountField, gen, triangulateButton, fpo, meb);
        setTop(toolbar);

        // Update the canvas size when the size of the BorderPane changes
        widthProperty().addListener((obs, oldVal, newVal) -> updateDrawPaneSize());
        heightProperty().addListener((obs, oldVal, newVal) -> updateDrawPaneSize());

        showVertexSet();
    }

    private void generate() {
        int pointCount = Integer.parseInt(pointCountField.getText());
        vertexSet = new VertexSet(pointCount);
        showVertexSet();
    }

    private void triangulate() {
        if (vertexSet != null) {
            vertexSet.delaunayTriangulate();
        }
        showVertexSet();
    }

    private void fpoIteration() {
        if (vertexSet != null) {
            FPOUtils.fpoIteration(vertexSet);
        }
        showVertexSet();
    }

    private void showMEB() {
        MiseEnBoite miseEnBoite = new RoundedCoordMeb();
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

    void showVertexSet() {
        drawPane.showVertexSet(vertexSet);
    }
}
