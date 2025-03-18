package local.ui.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.Vertex;

public class MediumDrawer extends Pane {
    private static final String BG_STYLE = "-fx-background-color: #FFFFFF;";

    public static boolean SHOW_POINTS = true;
    public static boolean SHOW_LINES = true;
    public static boolean SHOW_CANNING = true;

    double xmax, ymax, xmin, ymin,
           width, height,
           paneWidth, paneHeight,
           margin,
           scaleX, scaleY,
           scale,
           offsetX, offsetY;

    int canningWidth, canningHeight;
    Vertex[][] canningGrid;


    Medium tmpMedium;
    HashMap<Vertex, VertexCoord> tmpCanning;

    HashSet<Vertex> visited;
    Vertex selection;

    public MediumDrawer() {
        super();
        this.selection = null;
        setStyle(BG_STYLE);
    }

    private void initEnv(Medium medium, HashMap<Vertex, VertexCoord> canning) {
        this.tmpMedium = medium;

        xmax = medium.getMaxX();
        xmin = medium.getMinX();
        ymax = medium.getMaxY();
        ymin = medium.getMinY();

        width = xmax - xmin;
        height = ymax - ymin;

        paneWidth = getWidth();
        paneHeight = getHeight();

        margin = 20;
        scaleX = (paneWidth - 2 * margin) / width;
        scaleY = (paneHeight - 2 * margin) / height;
        scale = Math.min(scaleX, scaleY); // keep an orthonormal space

        offsetX = (paneWidth - width * scale) / 2;
        offsetY = (paneHeight - height * scale) / 2;

        if (canning == null) return;

        this.tmpCanning = canning;
        for (VertexCoord coord : canning.values()) {
            canningWidth = Math.max(canningWidth, coord.X());
            canningHeight = Math.max(canningHeight, coord.Y());
        }
        canningWidth++;
        canningHeight++;

        canningGrid = new Vertex[canningWidth][canningHeight];
        for (Vertex vertex : medium) {
            VertexCoord coord = canning.get(vertex);
            canningGrid[coord.X()][coord.Y()] = vertex;
        }
    }

    public void showMedium(Medium medium, HashMap<Vertex, VertexCoord> canning) {
        setStyle(BG_STYLE);
        if (medium == null) {
            getChildren().clear();
            getChildren().add(new Label("Nothing to show"));
            return;
        }

        initEnv(medium, canning);
        getChildren().clear();

        if (SHOW_LINES) drawLines();
        if (SHOW_CANNING) drawCanning();
        if (SHOW_POINTS) drawPoints();
    }

    private void drawPoint(Vertex v) {
        double x = (v.getX() - xmin) * scale + offsetX;
        double y = (v.getY() - ymin) * scale + offsetY;

        Circle circle = new Circle(x, y, 5);
        Circle selectionCircle = new Circle(x, y, 7); // Circle the selected point
        selectionCircle.setFill(null);
        selectionCircle.setStroke(Color.YELLOW);
        selectionCircle.setStrokeWidth(4);
        int neighborCount = v.getNeighbors().size();

        if (tmpMedium.partOfBorder(v)) {
            circle.setFill(Color.GREEN);
        } else {
            Color color = getColorFromNeighborCount(neighborCount);
            circle.setFill(color);
        }

        circle.setOnMouseClicked(event -> {
            if (selection == v) selection = null;
            else selection = v;
            redrawPoints();
        });
        getChildren().add(circle);

        if (selection == v) {
            getChildren().add(selectionCircle);
            for (Vertex neighbor : v.getNeighbors()) {
                double neighborX = (neighbor.getX() - xmin) * scale + offsetX;
                double neighborY = (neighbor.getY() - ymin) * scale + offsetY;
                Circle neighborSelectionCircle = new Circle(neighborX, neighborY, 7);
                getChildren().add(neighborSelectionCircle);
            }
        }
    }

    private void redrawPoints() {
        getChildren().clear();
        drawLines();
        drawPoints();
    }

    private void drawPoints() {
        forAllVertex(this::drawPoint);
    }

    private void drawLine(Vertex v) {
        double x = (v.getX() - xmin) * scale + offsetX;
        double y = (v.getY() - ymin) * scale + offsetY;

        for (Vertex neighbor : v.getNeighbors()) {
            double neighborX = (neighbor.getX() - xmin) * scale + offsetX;
            double neighborY = (neighbor.getY() - ymin) * scale + offsetY;
            double gradientStartX = x / paneWidth;
            double gradientStartY = y / paneHeight;
            double gradientEndX = neighborX / paneWidth;
            double gradientEndY = neighborY / paneHeight;

            Line line = new Line(x, y, neighborX, neighborY);
            Color color1 = getColorFromNeighborCount(v.getNeighbors().size());
            Color color2 = getColorFromNeighborCount(neighbor.getNeighbors().size());
            if (SHOW_CANNING && tmpCanning != null) {
                color1 = Color.LIGHTGRAY;
                color2 = Color.LIGHTGRAY;
            }
            Stop[] stops = new Stop[] {new Stop(0, color1), new Stop(1, color2)};
            LinearGradient linearGradient = new LinearGradient(
                    gradientStartX, gradientStartY, gradientEndX, gradientEndY, true, CycleMethod.NO_CYCLE, stops);
            line.setStroke(linearGradient);
            line.setStrokeWidth(2);

            getChildren().add(line);
        }
    }

    private void drawCanning() {
        if (tmpCanning == null) return;

        for (int j = 0; j < canningHeight; j++) {
            int i1 = 0;
            int i2 = 0;

            Vertex v1 = canningGrid[i1][j];
            while (v1 == null && i1 < canningWidth-1) {
                i1++;
                v1 = canningGrid[i1][j];
            }

            i2 = i1 + 1;
            Vertex v2;
            if (i2 < canningWidth) v2 = canningGrid[i2][j];
            else continue;
            while (v2 == null && i2 < canningWidth-1) {
                i2++;
                v2 = canningGrid[i2][j];
            }

            if (v1 == null || v2 == null) continue;
            do {
                double x1 = (v1.getX() - xmin) * scale + offsetX;
                double y1 = (v1.getY() - ymin) * scale + offsetY;
                double x2 = (v2.getX() - xmin) * scale + offsetX;
                double y2 = (v2.getY() - ymin) * scale + offsetY;

                Line line = new Line(x1, y1, x2, y2);
                line.setStroke(Color.GREEN);
                line.setStrokeWidth(2);

                getChildren().add(line);

                if (i2 >= canningWidth-1) break;

                i1 = i2;
                v1 = v2;

                do {
                    i2++;
                    v2 = canningGrid[i2][j];
                } while (v2 == null && i2 < canningWidth-1);

            } while (i1 < canningWidth && i2 < canningWidth && v2 != null);
        }

        for (int i = 0; i < canningWidth; i++) {
            int j1 = 0;
            int j2 = 0;

            Vertex v1 = canningGrid[i][j1];
            while (v1 == null && j1 < canningHeight-1) {
                j1++;
                v1 = canningGrid[i][j1];
            }

            j2 = j1 + 1;
            Vertex v2;
            if (j2 < canningHeight) v2 = canningGrid[i][j2];
            else continue;
            while (v2 == null && j2 < canningHeight-1) {
                j2++;
                v2 = canningGrid[i][j2];
            }

            if (v1 == null || v2 == null) continue;
            do {
                double x1 = (v1.getX() - xmin) * scale + offsetX;
                double y1 = (v1.getY() - ymin) * scale + offsetY;
                double x2 = (v2.getX() - xmin) * scale + offsetX;
                double y2 = (v2.getY() - ymin) * scale + offsetY;

                Line line = new Line(x1, y1, x2, y2);
                line.setStroke(Color.GOLD);
                line.setStrokeWidth(2);

                getChildren().add(line);

                if (j2 >= canningHeight-1) break;

                j1 = j2;
                v1 = v2;

                do {
                    j2++;
                    v2 = canningGrid[i][j2];
                } while (v2 == null && j2 < canningHeight-1);

            } while (j1 < canningHeight && j2 < canningHeight && v2 != null);
        }
    }

    private void drawLines() {
        forAllVertex(this::drawLine);
    }

    private void forAllVertex(Consumer<Vertex> action) {
        visited = new HashSet<>();
        for (Vertex v : this.tmpMedium) {
            action.accept(v);
            visited.add(v);
        }
    }

    private Color getColorFromNeighborCount(int count) {
        if (count == 0)
            return Color.BLACK;
        int criticalCount = 6;
        double sigmoid = 1 / (1 + Math.exp(-(count - criticalCount)));
        return Color.BLUE.interpolate(Color.RED, sigmoid);
    }
}
