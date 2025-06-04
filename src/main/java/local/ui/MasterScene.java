package local.ui;

import java.util.ArrayList;

import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import local.ui.mediumApps.*;

/**
 * MasterScene is the main scene that allows switching between different medium applications. <br>
 * It provides methods to facilitate the addition of new medium applications
 */
public class MasterScene extends BorderPane {
    static MasterScene instance;
    public static MasterScene getInstance() {
        if (instance == null)
            instance = new MasterScene();
        return instance;
    }

    final ToolBar toolBar;
    final ArrayList<SubScene> scenes;
    final ArrayList<MediumApp> mediumScenes;

    private MasterScene() {
        toolBar = new ToolBar();
        scenes = new ArrayList<>();
        mediumScenes = new ArrayList<>();

        addScene(new SoftSquareApp(), "Soft Square");
        addScene(new SoftCircleApp(), "Soft Circle");
        addScene(new HardRectangleApp(1), "Hard Square");
        addScene(new HardRectangleApp(Math.sqrt(3)), "Hard sqrt3:1 Rectangle");

        setTop(toolBar);
        setCenter(scenes.getFirst());

        widthProperty().addListener((obs, oldVal, newVal) -> updateWidth(newVal));
        heightProperty().addListener((obs, oldVal, newVal) -> updateHeight(newVal));

        updateWidth(getWidth()-1);
        updateHeight(getHeight()-1);
    }

    private void updateWidth(Number newVal) {
        for (SubScene subScene : scenes)
            subScene.setWidth(newVal.doubleValue());
    }
    private void updateHeight(Number newVal) {
        for (SubScene subScene : scenes)
            subScene.setHeight(newVal.doubleValue() - toolBar.getHeight());
    }

    /**
     * Adds a new medium application to the MasterScene, and creates a button in the toolbar to switch to it.
     * @param scene The MediumApp instance to be added.
     * @param name The name of the application, used for the button in the toolbar.
     */
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
