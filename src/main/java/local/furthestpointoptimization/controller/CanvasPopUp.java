package local.furthestpointoptimization.controller;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;

public class CanvasPopUp {
    public static void create(VertexSet vertexSet, int size) {
        Stage stage = new Stage();
        CanvasPopUp canvasController = new CanvasPopUp(vertexSet, size);
        stage.setScene(new Scene(new Pane(canvasController.getCanvas()), size, size));
        stage.show();
    }

    private final Canvas canvas = new Canvas();

    public CanvasPopUp(VertexSet vertexSet, int size) {
        canvas.setWidth(size*vertexSet.getWidth());
        canvas.setHeight(size*vertexSet.getHeight());

        for (Vertex vertex : vertexSet) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.fillOval(vertex.getX()*size-2, vertex.getY()*size-2, 4, 4);
            for (Vertex neighbor : vertex.getNeighbors()) {
                gc.strokeLine(
                    vertex.getX()*size   , vertex.getY()*size,
                    neighbor.getX()*size, neighbor.getY()*size
                );
            }
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
