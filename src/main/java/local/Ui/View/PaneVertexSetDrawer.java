package local.Ui.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import local.furthestpointoptimization.model.Point;
import local.furthestpointoptimization.model.optimisation.Triangle;
import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;

public class PaneVertexSetDrawer extends Pane {

    InformationBar infoBar;

    double xmax, ymax, xmin, ymin,
            width, height,
            paneWidth, paneHeight,
            margin,
            scaleX, scaleY,
            scale,
            offsetX, offsetY;

    VertexSet tmpvertexSet;
    HashSet<Vertex> visited;
    ArrayList<Vertex> selection;

    public PaneVertexSetDrawer(InformationBar infoBar, ArrayList<Vertex> selection) {
        super();
        this.infoBar = infoBar;
        this.selection = selection;
        setStyle("-fx-background-color: #EEEEEE;");
    }

    private void initEnv(VertexSet vertexSet) {
        this.tmpvertexSet = vertexSet;
        xmax = vertexSet.getMaxX();
        xmin = vertexSet.getMinX();
        ymax = vertexSet.getMaxY();
        ymin = vertexSet.getMinY();

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
    }

    public void showVertexSet(VertexSet vertexSet) {
        if (vertexSet == null) {
            ObservableList<Node> c = getChildren();
            c.clear();
            setStyle("-fx-background-color: #EEEEEE;");
            c.add(new Label("Nothing to show"));
            return;
        }

        initEnv(vertexSet);
        getChildren().clear();
        setStyle("-fx-background-color: #EEEEEE;");

        // drawTriangles();
        drawLines();
        drawPoints();

    }

    private void drawPoint(Vertex v) {
        double x = (v.getX() - xmin) * scale + offsetX;
        double y = (v.getY() - ymin) * scale + offsetY;

        Circle circle = new Circle(x, y, 5);
        Circle selectionCircle = new Circle(x, y, 7); // Cercle pour entourer le point sélectionné
        selectionCircle.setFill(null);
        selectionCircle.setStroke(Color.YELLOW);
        selectionCircle.setStrokeWidth(4);
        int neighborCount = v.getNeighbors().size();

        if (v.isBorder()) {
            circle.setFill(Color.GREEN);
        } else {
            Color color = getColorFromNeighborCount(neighborCount); // Couleur opaque
            circle.setFill(color);
        }

        circle.setOnMouseEntered(event -> {
            Point p = get_coordinate(v);
            String xs = (p == null) ? "?" : Integer.toString((int) p.getX());
            String ys = (p == null) ? "?" : Integer.toString((int) p.getY());
            infoBar.setText("Coordinates : (" + xs + ", " + ys + "), Neighbors: " + neighborCount);
        });

        circle.setOnMouseExited(event -> {infoBar.removeText();showDistance();});

        circle.setOnMouseClicked(event -> {
            
            if (selection.contains(v)) {
                selection.remove(v);
            } else {
                if (selection.size() == 2) {
                    selection.removeFirst();
                }
                selection.addLast(v);
            }
            showDistance();
            redrawPoints();
        });

        getChildren().add(circle);

        if (selection.contains(v)) {
            getChildren().add(selectionCircle);
        }
    }

    private void redrawPoints() {
        getChildren().clear();
        drawLines();
        drawPoints();
    }

    private void showDistance() {
        if (selection.size() == 0) {
            return;
        }
        if (selection.size() == 1) {
            String s = new String("Selected vertex : ");
            Vertex v = selection.getFirst();
            Point co = get_coordinate(v);
            if (co==null) {
                s+="(?, ?)";
            } else {
                double x = co.getX();
                double y = co.getY();
                s += "("+x+", "+y+")";
            }
            infoBar.setText(s);
        }
        if (selection.size() == 2) {
            String s = new String("Selected vertex : ");
            Vertex v1 = selection.getFirst();
            Point co1 = get_coordinate(v1);
            if (co1 != null) {
                double x1 = co1.getX();
                double y1 = co1.getY();
                s += "("+x1+","+y1+")";
            } else {
                s += "(?, ?)";
            }
            s += " - ";
            Vertex v2 = selection.getLast();
            Point co2 = get_coordinate(v2);
            if (co2 != null) {
                double x2 = co2.getX();
                double y2 = co2.getY();
                s += "("+x2+","+y2+")";
            } else {
                s += "(¿, ¿)";
            }
            infoBar.setText(s);
            
        }
    }

    private void drawPoints() {
        forAllVertex(vertex -> drawPoint(vertex));
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
            Stop[] stops = new Stop[] {
                    new Stop(0, getColorFromNeighborCount(v.getNeighbors().size())),
                    new Stop(1, getColorFromNeighborCount(neighbor.getNeighbors().size()))
            };
            LinearGradient linearGradient = new LinearGradient(
                    gradientStartX, gradientStartY, gradientEndX, gradientEndY, true, CycleMethod.NO_CYCLE, stops);
            line.setStroke(linearGradient);
            line.setStrokeWidth(2);

            line.setOnMouseEntered(event -> {
                Point p = get_coordinate(v);
                String xs = (p == null) ? "?" : Integer.toString((int) p.getX());
                String ys = (p == null) ? "?" : Integer.toString((int) p.getY());
                Point pn = get_coordinate(neighbor);
                String nxs = (pn == null) ? "¿" : Integer.toString((int) pn.getX());
                String nys = (pn == null) ? "¿" : Integer.toString((int) pn.getY());
                infoBar.setText("link (" + xs + ", " + ys + ") to (" + nxs + ", " + nys + ")");
            });

            line.setOnMouseExited(event -> {infoBar.removeText();showDistance();});
            getChildren().add(line);
        }
    }

    private void drawLines() {
        forAllVertex(vertex -> drawLine(vertex));
    }

    private void drawTriangles() {
        for (Triangle t : this.tmpvertexSet.getTriangles()) {
            Point pa = t.getA();
            Point pb = t.getB();
            Point pc = t.getC();

            Polygon triangle = new Polygon();
            triangle.getPoints().addAll(
                    (pa.getX() - xmin) * scale + offsetX, (pa.getY() - ymin) * scale + offsetY,
                    (pb.getX() - xmin) * scale + offsetX, (pb.getY() - ymin) * scale + offsetY,
                    (pc.getX() - xmin) * scale + offsetX, (pc.getY() - ymin) * scale + offsetY);

            Color ca = this.getColorFromNeighborCount(this.tmpvertexSet.getVertex(pa).getNeighbors().size());
            Color cb = this.getColorFromNeighborCount(this.tmpvertexSet.getVertex(pb).getNeighbors().size());
            Color cc = this.getColorFromNeighborCount(this.tmpvertexSet.getVertex(pc).getNeighbors().size());

            // Paint pain = createCustomImagePattern(pa, pb, pc, scale, offsetX, offsetY,
            // ca, cb, cc);
            triangle.setFill(Color.rgb(0, 0, 0, 0.1));

            getChildren().add(triangle);
        }
    }

    private void forAllVertex(Consumer<Vertex> action) {
        visited = new HashSet<>();
        for (Vertex v : this.tmpvertexSet) {
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

    private Point get_coordinate(Vertex v) {

        return null;
    }
}
