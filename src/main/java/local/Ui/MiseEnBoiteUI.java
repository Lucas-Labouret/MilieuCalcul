package local.Ui;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import local.Ui.View.InformationBar;
import local.Ui.View.PaneVertexSetDrawer;
import local.Ui.View.TBIntInput;
import local.furthestpointoptimization.model.miseEnBoite.Coord;
import local.furthestpointoptimization.model.miseEnBoite.MiseEnBoite;
import local.furthestpointoptimization.model.miseEnBoite.TopDistanceXSortedLinesMeb;
import local.furthestpointoptimization.model.optimisation.FPOUtils;
import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;

public class MiseEnBoiteUI extends BorderPane {

    ToolBar toolbar;
    private TBIntInput ptCountInput;
    private TBIntInput heightInput;
    InformationBar infoBar;
    // Pane drawPane;
    PaneVertexSetDrawer drawPane;
    VertexSet vertexSet;

    ArrayList<Vertex> selection;

    ArrayList<Vertex>[][] boite;

    public MiseEnBoiteUI() {
        super();
        this.infoBar = new InformationBar("Information");
        setBottom(infoBar);

        ptCountInput = new TBIntInput("Count", "20");
        heightInput = new TBIntInput("Height", "7");

        Button gen = new Button("Generate");
        gen.setOnAction((event) -> this.generate());
        Button fpo = new Button("FPO");
        fpo.setOnAction(e -> this.fpoIteration());
        Button MEB = new Button("Met en boite");
        MEB.setOnAction(e -> this.showMEB());

        toolbar = new ToolBar();
        toolbar.getItems().addAll(ptCountInput, heightInput, gen, fpo, MEB);
        setTop(toolbar);

        // drawPane = new Pane();
        selection = new ArrayList<>();
        drawPane = new PaneVertexSetDrawer(infoBar, selection);
        setCenter(drawPane);

        widthProperty().addListener((obs, oldVal, newVal) -> updateDrawPaneSize());
        heightProperty().addListener((obs, oldVal, newVal) -> updateDrawPaneSize());
        updateDrawPaneSize();
    }

    void showVertexSet() {
        drawPane.showVertexSet(vertexSet);
    }

    private void fpoIteration() {
        if (vertexSet != null) {
            FPOUtils.fpoIteration(vertexSet);
        }
        showVertexSet();
    }

    private void generate() {
        int pointCount = this.ptCountInput.getValue();
        int height = this.heightInput.getValue();
        int width = (int) (height * Math.sqrt(2));
        vertexSet = VertexSet.newHexBorderedSet(width, height, pointCount);
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
