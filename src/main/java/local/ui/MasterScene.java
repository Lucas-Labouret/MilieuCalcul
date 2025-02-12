package local.ui;

import java.util.ArrayList;

import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import local.computingMedium.miseEnBoite.RoundedCoordMeb;
import local.computingMedium.miseEnBoite.TopDistanceXSortedLinesMeb;
import local.ui.vertexSetScene.*;
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
        addApp(new HardSquareScene(), "Hard Square");
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

        sidePanel.getChildren().add(new Label("MEB"));
        sidePanel.getChildren().addAll(defaultMeb, roundedCoordMeb, topDistanceXSorted);
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
