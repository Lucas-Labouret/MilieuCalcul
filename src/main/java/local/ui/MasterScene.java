package local.ui;

import java.util.ArrayList;

import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import local.computingMedia.canning.RoundedCoordVCanning;
import local.computingMedia.canning.TopDistanceXSortedLinesVCanning;
import local.ui.mediumApps.*;
import local.ui.utils.MediumDrawer;
import local.ui.utils.SidePanel;

// Singleton
public class MasterScene extends BorderPane {
    static MasterScene instance;
    public static MasterScene getInstance() {
        if (instance == null)
            instance = new MasterScene();
        return instance;
    }

    final ToolBar toolBar;
    final SidePanel sidePanel;
    final ArrayList<SubScene> scenes;
    final ArrayList<MediumApp> mediumScenes;

    private MasterScene() {
        toolBar = new ToolBar();
        sidePanel = new SidePanel();
        scenes = new ArrayList<>();
        mediumScenes = new ArrayList<>();

        addScene(new SoftSquareApp(), "Soft Square");
        addScene(new SoftCircleApp(), "Soft Circle");
        addScene(new HardRectangleApp(1), "Hard Square");
        addScene(new HardRectangleApp(Math.sqrt(3)), "Hard sqrt3:1 Rectangle");

        fillSidePanel();

        setTop(toolBar);
        setCenter(scenes.getFirst());
        setRight(sidePanel);

        widthProperty().addListener((obs, oldVal, newVal) -> updateWidth(newVal));
        heightProperty().addListener((obs, oldVal, newVal) -> updateHeight(newVal));

        updateWidth(getWidth()-1);
        updateHeight(getHeight()-1);
    }

    private void updateWidth(Number newVal) {
        sidePanel.setPrefWidth(Math.min(200, newVal.doubleValue() / 3));
        for (SubScene subScene : scenes)
            subScene.setWidth(newVal.doubleValue() - sidePanel.getWidth());
    }
    private void updateHeight(Number newVal) {
        for (SubScene subScene : scenes)
            subScene.setHeight(newVal.doubleValue() - toolBar.getHeight());
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
            MediumDrawer.SHOW_POINTS = newVal;
            for (MediumApp scene : mediumScenes) scene.showVertexSet();
        });
        showLines.selectedProperty().addListener((obs, oldVal, newVal) -> {
            MediumDrawer.SHOW_LINES = newVal;
            for (MediumApp scene : mediumScenes) scene.showVertexSet();
        });

        //Canning
        ToggleGroup canGroup = new ToggleGroup();

        RadioButton defaultCanning = new RadioButton("Default");
        RadioButton roundedCoordCanning = new RadioButton("Rounded Coordinates");
        RadioButton topDistanceXSortedCanning = new RadioButton("Top Distance X Sorted");

        defaultCanning.setToggleGroup(canGroup);
        roundedCoordCanning.setToggleGroup(canGroup);
        topDistanceXSortedCanning.setToggleGroup(canGroup);

        defaultCanning.setSelected(true);

        canGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == defaultCanning) {
                for (MediumApp scene : mediumScenes) scene.setCanning(scene.DEFAULT_CANNING());
            } else if (newVal == roundedCoordCanning) {
                for (MediumApp scene : mediumScenes) scene.setCanning(new RoundedCoordVCanning());
            } else if (newVal == topDistanceXSortedCanning) {
                for (MediumApp scene : mediumScenes) scene.setCanning(new TopDistanceXSortedLinesVCanning());
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
            for (MediumApp scene : mediumScenes)
                scene.setFpoToConvergence(newVal == toConvergence);
        });

        iterInput.textProperty().addListener((obs, oldVal, newVal) -> {
            int iter;
            try { iter = Integer.parseInt(newVal); } 
            catch (NumberFormatException e) { return; }
        
            for (MediumApp scene : mediumScenes)
                scene.setFpoIterations(iter);
        });

        convInput.textProperty().addListener((obs, oldVal, newVal) -> {
            double conv;
            try { conv = Double.parseDouble(newVal); } 
            catch (NumberFormatException e) { return; }
        
            for (MediumApp scene : mediumScenes)
                scene.setConvergenceTolerance(conv);
        });


        //Add to side panel
        sidePanel.getChildren().add(new Label("Graphics"));
        sidePanel.getChildren().addAll(showPoints, showLines);

        sidePanel.getChildren().add(new Label("Canning"));
        sidePanel.getChildren().addAll(defaultCanning, roundedCoordCanning, topDistanceXSortedCanning);

        sidePanel.getChildren().add(new Label("FPO"));
        sidePanel.getChildren().addAll(setIter, iterInput, toConvergence, convInput);
    }

    public void addScene(MediumApp scene, String name) {
        SubScene subScene = new SubScene(scene, 500, 500);
        scenes.add(subScene);
        mediumScenes.add(scene);
        Button b = new Button(name);
        b.setOnAction((event) -> {
            System.out.println("Set " + name + " in center");
            setCenter(subScene);
        });
        this.toolBar.getItems().add(b);
    }

}
