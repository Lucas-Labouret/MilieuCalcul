package local.Ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;

public class Triangulation2DView extends BorderPane {

    ToolBar toolbar;
    Canvas canvas;
    TextField pointCountField;

    private VertexSet vertexSet;

    public Triangulation2DView() {
        super();
        toolbar = new ToolBar();

        pointCountField = new TextField("20");
        toolbar.getItems().add(pointCountField);
        Button gen = new Button("Generate");
        gen.setOnAction((event) -> {
            int pointCount = Integer.parseInt(pointCountField.getText());
            vertexSet = new VertexSet(pointCount);
            vertexSet.triangulate();
            showVertexSet();
        });
        toolbar.getItems().add(gen);

        setTop(toolbar);

        canvas = new Canvas();
        setCenter(new Label("Nothing to show"));

        // Update the canvas size when the size of the BorderPane changes
        widthProperty().addListener((obs, oldVal, newVal) -> updateCanvasSize());
        heightProperty().addListener((obs, oldVal, newVal) -> updateCanvasSize());
    }

    private void updateCanvasSize() {
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight() - toolbar.getHeight());
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (vertexSet != null) {
            showVertexSet();
        }
    }

    void showVertexSet() {
        double xmax = vertexSet.getMaxX(), xmin = vertexSet.getMinX();
        double ymax = vertexSet.getMaxY(), ymin = vertexSet.getMinY();

        double width = xmax - xmin;
        double height = ymax - ymin;

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        double margin = 20;
        double scaleX = (canvasWidth - 2 * margin) / width;
        double scaleY = (canvasHeight - 2 * margin) / height;
        double scale = Math.min(scaleX, scaleY);

        double offsetX = (canvasWidth - width * scale) / 2;
        double offsetY = (canvasHeight - height * scale) / 2;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.strokeLine(margin, margin, canvasWidth - margin, margin);
        gc.strokeLine(margin, canvasHeight - margin, canvasWidth - margin, canvasHeight - margin);
        gc.strokeLine(margin, margin, margin, canvasHeight - margin);
        gc.strokeLine(canvasWidth - margin, margin, canvasWidth - margin, canvasHeight - margin);

        for (Vertex v : vertexSet) {
            double x = (v.getX() - xmin) * scale + offsetX;
            double y = (v.getY() - ymin) * scale + offsetY;
            gc.fillOval(x - 5, y - 5, 10, 10);

            for (Vertex neighbor : v.getNeighbors()) {
                double neighborX = (neighbor.getX() - xmin) * scale + offsetX;
                double neighborY = (neighbor.getY() - ymin) * scale + offsetY;
                gc.strokeLine(x, y, neighborX, neighborY);
            }
        }
        setCenter(canvas);
    }
}
