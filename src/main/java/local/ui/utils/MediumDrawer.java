package local.ui.utils;

import java.util.HashSet;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import local.computingMedia.cannings.Canning;
import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.Edge;
import local.computingMedia.sLoci.Face;
import local.computingMedia.sLoci.Vertex;
import local.computingMedia.tLoci.*;

public class MediumDrawer extends Pane {
    private static final String BG_STYLE = "-fx-background-color: #FFFFFF;";
    private static final int SLOCI_RADIUS = 5;
    private static final double TLOCI_SPACING = 0.33;

    private boolean SHOW_VERTICES = true;
    private boolean SHOW_EDGES = true;
    private boolean EDGES_AS_LINES = true;
    private boolean SHOW_FACES = false;

    private boolean SHOW_CANNING = false;
    private boolean SHOW_EF_FE = false;
    private boolean SHOW_EV_VE = false;
    private boolean SHOW_FV_VF = false;


    double xmax, ymax, xmin, ymin,
           width, height,
           paneWidth, paneHeight,
           margin,
           scaleX, scaleY,
           scale,
           offsetX, offsetY;

    int canningWidth, canningHeight;
    Vertex[][] canningGrid;

    Medium medium;
    Canning canning;

    HashSet<Vertex> visited;
    Vertex selection;

    public MediumDrawer(Medium medium, Canning canning) {
        this.medium = medium;
        this.canning = canning;
        this.selection = null;
        setStyle(BG_STYLE);
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
        redraw();
    }
    public void setCanning(Canning canning) {
        this.canning = canning;
        redraw();
    }

    public void setShowVertices(boolean showVertices) {
        SHOW_VERTICES = showVertices;
        redraw();
    }
    public void setShowEdges(boolean showEdges) {
        SHOW_EDGES = showEdges;
        redraw();
    }
    public void setEdgesAsLines(boolean edgesAsLines) {
        EDGES_AS_LINES = edgesAsLines;
        redraw();
    }
    public void setShowFaces(boolean showFaces) {
        SHOW_FACES = showFaces;
        redraw();
    }

    public void setShowCanning(boolean showCanning) {
        SHOW_CANNING = showCanning;
        redraw();
    }
    public void setShowEfFe(boolean showEfFe) {
        SHOW_EF_FE = showEfFe;
        redraw();
    }
    public void setShowEvVe(boolean showEvVe) {
        SHOW_EV_VE = showEvVe;
        redraw();
    }
    public void setShowFvVf(boolean showFvVf) {
        SHOW_FV_VF = showFvVf;
        redraw();
    }

    private void initEnv() {
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
        canning.can();

        for (VertexCoord coord : canning.getVertexCanning().values()) {
            canningWidth = Math.max(canningWidth, coord.X());
            canningHeight = Math.max(canningHeight, coord.Y());
        }
        canningWidth++;
        canningHeight++;

        canningGrid = new Vertex[canningWidth][canningHeight];
        for (Vertex vertex : medium) {
            VertexCoord coord = canning.getVertexCanning().get(vertex);
            canningGrid[coord.X()][coord.Y()] = vertex;
        }
    }

    public void redraw() {
        setStyle(BG_STYLE);
        if (medium == null) {
            getChildren().clear();
            getChildren().add(new Label("Nothing to show"));
            return;
        }

        initEnv();
        getChildren().clear();

        if (SHOW_EDGES && EDGES_AS_LINES ) drawLines();
        if (SHOW_EDGES && !EDGES_AS_LINES) drawEdges();
        if (SHOW_FACES                   ) drawFaces();
        if (SHOW_CANNING                 ) drawCanning();
        if (SHOW_EF_FE                   ) drawEfFe();
        if (SHOW_EV_VE                   ) drawEvVe();
        if (SHOW_FV_VF                   ) drawFvVf();
        if (SHOW_VERTICES                ) drawVertices();
    }

    private Color getColorFromNeighborCount(int count) {
        if (count == 0)
            return Color.BLACK;
        int criticalCount = 6;
        double sigmoid = 1 / (1 + Math.exp(-(count - criticalCount)));
        return Color.BLUE.interpolate(Color.RED, sigmoid);
    }

    private void drawVertices() {
        for (Vertex v : medium) {
            double x = (v.getX() - xmin) * scale + offsetX;
            double y = (v.getY() - ymin) * scale + offsetY;

            Circle circle = new Circle(x, y, SLOCI_RADIUS);
            Circle selectionCircle = new Circle(x, y, (int)(1.5*SLOCI_RADIUS)); // Circle the selected point
            selectionCircle.setFill(null);
            selectionCircle.setStroke(Color.YELLOW);
            selectionCircle.setStrokeWidth(4);
            int neighborCount = v.getNeighbors().size();

            if (medium.partOfBorder(v)) {
                circle.setFill(Color.GREEN);
            } else {
                Color color = getColorFromNeighborCount(neighborCount);
                circle.setFill(color);
            }

            circle.setOnMouseClicked(event -> {
                if (selection == v) selection = null;
                else selection = v;
                redraw();
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
    }

    private void drawLines() {
        for (Edge e : medium.getEdges()) {
            Vertex[] ends = e.getEnds().toArray(new Vertex[2]);
            Vertex v1 = ends[0];
            Vertex v2 = ends[1];

            double x1 = (v1.getX() - xmin) * scale + offsetX;
            double y1 = (v1.getY() - ymin) * scale + offsetY;

            double x2 = (v2.getX() - xmin) * scale + offsetX;
            double y2 = (v2.getY() - ymin) * scale + offsetY;

            double gradientStartX = x1 / paneWidth;
            double gradientStartY = y1 / paneHeight;
            double gradientEndX = x2 / paneWidth;
            double gradientEndY = y2 / paneHeight;

            Line line = new Line(x1, y1, x2, y2);
            Color color1 = getColorFromNeighborCount(v1.getNeighbors().size());
            Color color2 = getColorFromNeighborCount(v2.getNeighbors().size());
            if (SHOW_CANNING && canning != null) {
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

    private void drawEdges() {
        for (Edge e : medium.getEdges()) {
            Vertex center = e.getCenter();
            double x = (center.getX() - xmin) * scale + offsetX;
            double y = (center.getY() - ymin) * scale + offsetY;

            Rectangle rectangle = new Rectangle(
                    x-SLOCI_RADIUS,
                    y-SLOCI_RADIUS,
                    2*SLOCI_RADIUS,
                    2*SLOCI_RADIUS
            );
            rectangle.setFill((SHOW_CANNING && canning != null)? Color.LIGHTGRAY: Color.GOLD);

            getChildren().add(rectangle);
        }
    }

    private void drawFaces() {
        for (Face f : medium.getFaces()) {
            Vertex center = f.getCentroid();
            double x = (center.getX() - xmin) * scale + offsetX;
            double y = (center.getY() - ymin) * scale + offsetY;

            Polygon triangle = new Polygon(
                    x, y - SLOCI_RADIUS,
                    x - SLOCI_RADIUS, y + SLOCI_RADIUS,
                    x + SLOCI_RADIUS, y + SLOCI_RADIUS
            );
            triangle.setFill((SHOW_CANNING && canning != null)? Color.LIGHTGRAY: Color.GREEN);

            getChildren().add(triangle);
        }
    }

    private void drawTLoci(Vertex v1, Vertex v2, Color color) {
        Vertex v = Edge.getWeightedCenter(TLOCI_SPACING, v1, v2);
        double x = (v.getX() - xmin) * scale + offsetX;
        double y = (v.getY() - ymin) * scale + offsetY;

        Circle circle = new Circle(x, y, SLOCI_RADIUS/2);
        circle.setFill(color);
        getChildren().add(circle);
    }

    private void drawEfFe() {
        for (Ef ef: canning.getEf()){
            Vertex v1 = ef.e().getCenter();
            Vertex v2 = ef.f().getCentroid();
            drawTLoci(v1, v2, Color.LIGHTPINK);
        }

        for (Fe fe : canning.getFe()){
            Vertex v1 = fe.f().getCentroid();
            Vertex v2 = fe.e().getCenter();
            drawTLoci(v1, v2, Color.DEEPPINK);
        }
    }

    private void drawEvVe() {
        for (Ev ev : canning.getEv()){
            Vertex v1 = ev.e().getCenter();
            Vertex v2 = ev.v();
            drawTLoci(v1, v2, Color.DEEPSKYBLUE);
        }

        for (Ve ve : canning.getVe()){
            Vertex v1 = ve.v();
            Vertex v2 = ve.e().getCenter();
            drawTLoci(v1, v2, Color.DARKBLUE);
        }
    }

    private void drawFvVf() {
        for (Fv fv : canning.getFv()){
            Vertex v1 = fv.f().getCentroid();
            Vertex v2 = fv.v();
            drawTLoci(v1, v2, Color.RED);
        }

        for (Vf vf : canning.getVf()){
            Vertex v1 = vf.v();
            Vertex v2 = vf.f().getCentroid();
            drawTLoci(v1, v2, Color.ORANGE);
        }
    }

    private void drawCanning() {
        if (canning == null) return;

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
}
