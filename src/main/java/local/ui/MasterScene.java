package local.ui;

import java.util.ArrayList;

import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import local.computingMedium.miseEnBoite.RoundedCoordMeb;
import local.computingMedium.miseEnBoite.TopDistanceXSortedLinesMeb;
import local.ui.vertexSetScene.*;
import local.ui.view.PaneVertexSetDrawer;
import local.ui.view.SidePanel;

// Singleton
public class MasterScene extends BorderPane {
    static MasterScene instance;
    public static MasterScene getInstance() {
        if (instance == null)
            instance = new MasterScene();
        return instance;
    }

    ToolBar toolBar;
    SidePanel sidePanel;
    ArrayList<SubScene> apps;
    ArrayList<VertexSetScene> vertexSetScenes;

    private MasterScene() {
        toolBar = new ToolBar();
        sidePanel = new SidePanel();
        apps = new ArrayList<>();
        vertexSetScenes = new ArrayList<>();

        addApp(new SoftSquareScene(), "Soft Square");
        addApp(new SoftCircleScene(), "Soft Circle");
        addApp(new HardHexScene(), "Hard Hex");
        addApp(new HardRectangleScene(1), "Hard Square");
        addApp(new HardRectangleScene(Math.sqrt(3)), "Hard sqrt3:1 Rectangle");
        addApp(new SceneLoader(), "Load");

        fillSidePanel();

        setTop(toolBar);
        setCenter(apps.getFirst());
        setRight(sidePanel);

        widthProperty().addListener((obs, oldVal, newVal) -> {
            updateWidth(newVal);
        });
        heightProperty().addListener((obs, oldVal, newVal) -> {
            updateHeight(newVal);
        });

        updateWidth(getWidth()-1);
        updateHeight(getHeight()-1);
    }

    private void updateWidth(Number newVal) {
        sidePanel.setPrefWidth(Math.min(200, newVal.doubleValue() / 3));
        for (SubScene sbscene : apps)
            sbscene.setWidth(newVal.doubleValue() - sidePanel.getWidth());
    }
    private void updateHeight(Number newVal) {
        for (SubScene sbscene : apps)
            sbscene.setHeight(newVal.doubleValue() - toolBar.getHeight());
    }

    private void fillSidePanel() {
        //Graphics
        CheckBox showPoints = new CheckBox("Show Points");
        CheckBox showLines = new CheckBox("Show Lines");

        showPoints.setSelected(true);
        showLines.setSelected(true);

        showPoints.allowIndeterminateProperty().set(false);
        showLines.allowIndeterminateProperty().set(false);

        showPoints.selectedProperty().addListener((obs, oldVal, newVal) -> {
            PaneVertexSetDrawer.SHOW_POINTS = newVal;
            for (VertexSetScene scene : vertexSetScenes) scene.showVertexSet();
        });
        showLines.selectedProperty().addListener((obs, oldVal, newVal) -> {
            PaneVertexSetDrawer.SHOW_LINES = newVal;
            for (VertexSetScene scene : vertexSetScenes) scene.showVertexSet();
        });

        //MEB
        ToggleGroup mebGroup = new ToggleGroup();

        RadioButton defaultMeb = new RadioButton("Default");
        RadioButton roundedCoordMeb = new RadioButton("Rounded Coordinates");
        RadioButton topDistanceXSorted = new RadioButton("Top Distance X Sorted");

        defaultMeb.setToggleGroup(mebGroup);
        roundedCoordMeb.setToggleGroup(mebGroup);
        topDistanceXSorted.setToggleGroup(mebGroup);

        defaultMeb.setSelected(true);

        mebGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == defaultMeb) {
                for (VertexSetScene scene : vertexSetScenes) scene.setMeb(scene.DEFAULT_MEB());
            } else if (newVal == roundedCoordMeb) {
                for (VertexSetScene scene : vertexSetScenes) scene.setMeb(new RoundedCoordMeb());
            } else if (newVal == topDistanceXSorted) {
                for (VertexSetScene scene : vertexSetScenes) scene.setMeb(new TopDistanceXSortedLinesMeb());
            }
        });
        
        //FPO
        ToggleGroup fpoGroup = new ToggleGroup();

        RadioButton setIter = new RadioButton("Set iterations");
        TextField iterInput = new TextField("1");

        RadioButton toConvergence = new RadioButton("To convergence");
        TextField convInput = new TextField("0.9");

        setIter.setToggleGroup(fpoGroup);
        toConvergence.setToggleGroup(fpoGroup);

        setIter.setSelected(true);

        fpoGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            for (VertexSetScene scene : vertexSetScenes) 
                scene.setFpoToConvergence(newVal == toConvergence);
        });

        iterInput.textProperty().addListener((obs, oldVal, newVal) -> {
            int iter;
            try { iter = Integer.parseInt(newVal); } 
            catch (NumberFormatException e) { return; }
        
            for (VertexSetScene scene : vertexSetScenes) 
                scene.setFpoIterations(iter);
        });

        convInput.textProperty().addListener((obs, oldVal, newVal) -> {
            double conv;
            try { conv = Double.parseDouble(newVal); } 
            catch (NumberFormatException e) { return; }
        
            for (VertexSetScene scene : vertexSetScenes) 
                scene.setConvergenceTolerance(conv);
        });


        //Add to side panel
        sidePanel.getChildren().add(new Label("Graphics"));
        sidePanel.getChildren().addAll(showPoints, showLines);

        sidePanel.getChildren().add(new Label("MEB"));
        sidePanel.getChildren().addAll(defaultMeb, roundedCoordMeb, topDistanceXSorted);

        sidePanel.getChildren().add(new Label("FPO"));
        sidePanel.getChildren().addAll(setIter, iterInput, toConvergence, convInput);
    }

    public void addApp(VertexSetScene scene, String name) {
        SubScene subApp = new SubScene(scene, 500, 500);
        apps.add(subApp);
        vertexSetScenes.add(scene);
        Button b = new Button(name);
        b.setOnAction((event) -> {
            System.out.println("Setted " + name + " in center");
            setCenter(subApp);
        });
        this.toolBar.getItems().add(b);
    }

}
