package local.ui;

import java.util.ArrayList;

import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import local.ui.vertexSetScene.HardHexScene;
import local.ui.vertexSetScene.SoftSquareScene;
import local.ui.view.SidePanel;

// Singleton
public class MasterScene extends BorderPane {
    static MasterScene instance;
    public static MasterScene getInstance() {
        if (instance == null)
            instance = new MasterScene();
        return instance;
    }

    // TOP
    ToolBar toolBar;
    // LEFT
    SidePanel sidePanel;
    // Center
    ArrayList<SubScene> apps;

    private MasterScene() {
        this.toolBar = new ToolBar();
        setTop(this.toolBar);
        this.sidePanel = new SidePanel();
        apps = new ArrayList<>();

        SoftSquareScene triangulationView = new SoftSquareScene();
        SubScene subScene = new SubScene(triangulationView, 500, 500);
        addApp(subScene, "Soft Square");
        setCenter(subScene);

        HardHexScene subapp2 = new HardHexScene();
        addApp(new SubScene(subapp2, 500, 500), "Hard Hex");

        widthProperty().addListener((obs, oldVal, newVal) -> {
            for (SubScene sbscene : apps)
                sbscene.setWidth(newVal.doubleValue());
        });

        heightProperty().addListener((obs, oldVal, newVal) -> {
            for (SubScene sbscene : apps)
                sbscene.setHeight(newVal.doubleValue() - toolBar.getHeight());
        });
    }

    public void addApp(SubScene subApp, String name) {
        this.apps.add(subApp);
        Button b = new Button(name);
        b.setOnAction((event) -> {
            System.out.println("Setted " + name + " in center");
            setCenter(subApp);
        });
        this.toolBar.getItems().add(b);
    }

}
