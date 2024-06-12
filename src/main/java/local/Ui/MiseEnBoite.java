package local.Ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import local.Ui.View.InformationBar;
import local.Ui.View.PaneVertexSetDrawer;
import local.Ui.View.TBIntInput;
import local.furthestpointoptimization.model.optimisation.FPOUtils;
import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;

public class MiseEnBoite extends BorderPane {

    ToolBar toolbar;
    private TBIntInput ptCountInput;
    private TBIntInput heightInput;
    InformationBar infoBar;
    // Pane drawPane;
    PaneVertexSetDrawer drawPane;
    VertexSet vertexSet;

    ArrayList<Vertex> selection;

    ArrayList<Vertex>[][] boite;

    public MiseEnBoite() {
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
        MEB.setOnAction(e -> {
            int count = this.ptCountInput.getValue();
            int l = (int)Math.sqrt(count);
            boite = new ArrayList[l+3][l+3];
            
            for (int i = 0; i < l+3; i++) {
                for (int j = 0; j < l+3; j++) {
                    boite[i][j] = new ArrayList<Vertex>();
                }
            }

            System.out.println("non je ne fais rien");
            Random rand = new Random();
            for (Vertex v : this.vertexSet) {
                while (true || false) {
                    int randomNum1 = rand.nextInt(l+2);
                    int randomNum2 = rand.nextInt(l+2);
                    if (boite[randomNum1][randomNum2].size()==0) {
                        boite[randomNum1][randomNum2].add(v);
                        break;
                    }
                } 
            }
        });

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

    private void updateDrawPaneSize() {
        drawPane.setPrefWidth(getWidth());
        drawPane.setPrefHeight(getHeight() - toolbar.getHeight() - infoBar.getHeight());
        if (vertexSet != null) {
            showVertexSet();
        }
    }
}
