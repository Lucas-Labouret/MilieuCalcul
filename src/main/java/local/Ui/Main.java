package local.Ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import local.Ui.View.MainLayout;
import local.Ui.View.ToolBarComponent;
import local.Ui.camera.SimpleFPSCamera;

public class Main extends Application {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 450;

    @Override
    public void start(Stage stage) throws Exception {
        blobStaging(stage);

        MainLayout ml = new MainLayout();

        Scene scene = new Scene(ml, Main.WIDTH, Main.HEIGHT, Color.SKYBLUE);

        stage.setScene(scene);
        stage.show();

    }

    private void blobStaging(Stage stage) {
        stage.setTitle("Blob");
        Image icon = new Image("blob.png");
        stage.getIcons().add(icon);
    }

    public static void main(String args[]) {
        launch(args);
    }
}