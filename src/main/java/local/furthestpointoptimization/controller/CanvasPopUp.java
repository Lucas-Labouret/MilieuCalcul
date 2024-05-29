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
        canvas.setWidth(size+20);
        canvas.setHeight(size+20);

        //System.out.println(vertexSet.getSize());

        for (Vertex vertex : vertexSet) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            //gc.fillText(Integer.toString(vertex.getId()), vertex.getX()*size + 5, vertex.getY()*size - 5);
            //gc.fillOval(vertex.getX()*size - 5, vertex.getY()*size - 5, 10, 10);
            //System.out.println(vertex.getX() + " " + vertex.getY() + " " + vertex.getNeighbors().size());
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
