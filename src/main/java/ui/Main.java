package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ui.optimization.MasterScene_opt;

import java.io.Console;

/**
 * Main class for the Blob application.
 * This class initializes the JavaFX application, and serves as the entry point.
 */
public class Main extends Application {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 450;

    @Override
    public void start(Stage stage) {
        blobStaging(stage);

        String app = getParameters().getRaw().getFirst();
        MasterScene ms;
        if (app != null && app.equals("computation")) {
            System.out.println("Starting computation application...");
            ms = ui.computation.MasterScene_comp.getInstance();
        } else if (app != null && app.equals("optimization")) {
            System.out.println("Starting optimization application...");
            ms = MasterScene_opt.getInstance();
        } else {
            System.out.println("No valid application specified. Starting optimization application by default...");
            ms = MasterScene_opt.getInstance();
        }

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