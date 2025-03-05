package local.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 450;

    @Override
    public void start(Stage stage) {
        blobStaging(stage);

        MasterScene ms = MasterScene.getInstance();

        Scene scene = new Scene(ms, Main.WIDTH, Main.HEIGHT, Color.SKYBLUE);

        stage.setScene(scene);
        stage.show();

    }

    private void blobStaging(Stage stage) {
        stage.setTitle("Blob");
        Image icon = new Image("blob.png");
        stage.getIcons().add(icon);
    }

    public static void main(String[] args) {
        launch(args);
    }
}