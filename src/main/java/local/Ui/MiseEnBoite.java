package local.Ui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import local.furthestpointoptimization.model.FPOUtils;
import local.furthestpointoptimization.model.Point;
import local.furthestpointoptimization.model.Triangle;
import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;

public class MiseEnBoite extends BorderPane {

    ToolBar toolbar;

    VBox pointCountBox;
    TextField pointCountField;

    VBox heightBox;
    TextField heightField;

    VertexSet vertexSet;

    Pane drawPane;

    Label infoBar;

    public MiseEnBoite() {
        super();
        Pane bottomRegion = new Pane();
        bottomRegion.setStyle("-fx-background-color: #CCCCCC;");
        infoBar = new Label("Information");
        bottomRegion.getChildren().add(infoBar);
        setBottom(bottomRegion);

        toolbar = new ToolBar();

        pointCountBox = new VBox();
        pointCountField = new TextField("20");
        pointCountField.setMaxWidth(40);
        pointCountBox.getChildren().addAll(new Label("Count"), pointCountField);
        toolbar.getItems().add(pointCountBox);

        heightBox = new VBox();
        heightBox.setAlignment(Pos.CENTER);
        heightField = new TextField("7");
        heightField.setMaxWidth(40);
        heightBox.getChildren().addAll(new Label("Height"), heightField);
        toolbar.getItems().add(heightBox);

        // Generate button
        Button gen = new Button("Generate");
        gen.setOnAction((event) -> {
            int pointCount = Integer.parseInt(pointCountField.getText());
            int height = Integer.parseInt(heightField.getText());
            int width = (int) (height * Math.sqrt(2));
            vertexSet = VertexSet.newHexBorderedSet(width, height, pointCount);
            showVertexSet();
        });

        Button fpo = new Button("FPO");
        fpo.setOnAction(event -> {
            if (vertexSet != null) {
                FPOUtils.fpoIteration(vertexSet);
            }
            showVertexSet();
        });

        toolbar.getItems().addAll(gen, fpo);
        setTop(toolbar);

        drawPane = new Pane();
        widthProperty().addListener((obs, oldVal, newVal) -> updateDrawPaneSize());
        heightProperty().addListener((obs, oldVal, newVal) -> updateDrawPaneSize());
        updateDrawPaneSize();

        setCenter(drawPane);
    }

    private void updateDrawPaneSize() {
        drawPane.setPrefWidth(getWidth());
        drawPane.setPrefHeight(getHeight() - toolbar.getHeight() - infoBar.getHeight());
        if (vertexSet != null) {
            showVertexSet();
        }
    }

    void showVertexSet() {
        if (vertexSet == null) {
            drawPane.getChildren().clear();
            drawPane.getChildren().add(new Label("Nothing to show"));
            return;
        }

        double xmax = vertexSet.getMaxX(), xmin = vertexSet.getMinX();
        double ymax = vertexSet.getMaxY(), ymin = vertexSet.getMinY();

        double width = xmax - xmin;
        double height = ymax - ymin;

        double paneWidth = drawPane.getWidth();
        double paneHeight = drawPane.getHeight();

        double margin = 20;
        double scaleX = (paneWidth - 2 * margin) / width;
        double scaleY = (paneHeight - 2 * margin) / height;
        double scale = Math.min(scaleX, scaleY);

        double offsetX = (paneWidth - width * scale) / 2;
        double offsetY = (paneHeight - height * scale) / 2;

        drawPane.getChildren().clear();

        for (Triangle t : vertexSet.getTriangles()) {
            Point pa = t.getA();
            Point pb = t.getB();
            Point pc = t.getC();

            Polygon triangle = new Polygon();
            triangle.getPoints().addAll(
                    (pa.getX() - xmin) * scale + offsetX, (pa.getY() - ymin) * scale + offsetY,
                    (pb.getX() - xmin) * scale + offsetX, (pb.getY() - ymin) * scale + offsetY,
                    (pc.getX() - xmin) * scale + offsetX, (pc.getY() - ymin) * scale + offsetY);

            Color ca = this.getColorFromNeighborCount(vertexSet.getVertex(pa).getNeighbors().size());
            Color cb = this.getColorFromNeighborCount(vertexSet.getVertex(pb).getNeighbors().size());
            Color cc = this.getColorFromNeighborCount(vertexSet.getVertex(pc).getNeighbors().size());

            // Paint pain = createCustomImagePattern(pa, pb, pc, scale, offsetX, offsetY,
            // ca, cb, cc);
            triangle.setFill(Color.rgb(0, 0, 0, 0.1));

            drawPane.getChildren().add(triangle);
        }

        VertexSet visited = new VertexSet();
        for (Vertex v : vertexSet) {
            double x = (v.getX() - xmin) * scale + offsetX;
            double y = (v.getY() - ymin) * scale + offsetY;

            for (Vertex neighbor : v.getNeighbors()) {
                if (visited.contains(neighbor))
                    continue;
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
                line.setStrokeWidth(1);

                line.setOnMouseEntered(event -> {
                    Point p = get_coordinate(v);
                    String xs = (p == null) ? "?" : Integer.toString((int) p.getX());
                    String ys = (p == null) ? "?" : Integer.toString((int) p.getY());
                    Point pn = get_coordinate(neighbor);
                    String nxs = (pn == null) ? "¿" : Integer.toString((int) pn.getX());
                    String nys = (pn == null) ? "¿" : Integer.toString((int) pn.getY());
                    infoBar.setText("link (" + xs + ", " + ys + ") to (" + nxs + ", " + nys + ")");
                });
                line.setOnMouseExited(event -> {
                    infoBar.setText("Information");
                });
                drawPane.getChildren().add(line);
            }
            visited.add(v);
        }

        for (Vertex v : vertexSet) {
            double x = (v.getX() - xmin) * scale + offsetX;
            double y = (v.getY() - ymin) * scale + offsetY;

            Circle circle = new Circle(x, y, 5);
            if (v.isBorder() && false) {
                circle.setFill(Color.GREEN);
            } else {
                int neighborCount = v.getNeighbors().size();
                Color color = getColorFromNeighborCount(neighborCount); // Couleur opaque
                circle.setFill(color);

                circle.setOnMouseEntered(event -> {
                    Point p = get_coordinate(v);
                    String xs = (p == null) ? "?" : Integer.toString((int) p.getX());
                    String ys = (p == null) ? "?" : Integer.toString((int) p.getY());
                    infoBar.setText("Coordinates : (" + xs + ", " + ys + "), Neighbors: " + neighborCount);
                });
                circle.setOnMouseExited(event -> {
                    infoBar.setText("Information");
                });

                circle.setOnMouseClicked(event -> {
                    circle.setFill(Color.RED);
                });
            }

            drawPane.getChildren().add(circle);
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

    // private ImagePattern createCustomImagePattern(Point pa, Point pb, Point pc,
    // double scale, double offsetX,
    // double offsetY, Color ca, Color cb, Color cc) {
    // // Calculer les dimensions de l'image
    // double minX = Math.min(pa.getX(), Math.min(pb.getX(), pc.getX()));
    // double minY = Math.min(pa.getY(), Math.min(pb.getY(), pc.getY()));
    // double maxX = Math.max(pa.getX(), Math.max(pb.getX(), pc.getX()));
    // double maxY = Math.max(pa.getY(), Math.max(pb.getY(), pc.getY()));
    // int imageWidth = (int) ((maxX - minX) * scale);
    // int imageHeight = (int) ((maxY - minY) * scale);

    // // Créer une image avec les dimensions calculées
    // WritableImage image = new WritableImage(imageWidth, imageHeight);
    // PixelWriter pixelWriter = image.getPixelWriter();

    // // Parcourir tous les pixels de l'image
    // for (int x = 0; x < imageWidth; x++) {
    // for (int y = 0; y < imageHeight; y++) {
    // // Convertir les coordonnées du pixel en coordonnées du VertexSet
    // double vertexSetX = (x / scale) + minX;
    // double vertexSetY = (y / scale) + minY;

    // // Vérifier si le pixel est à l'intérieur du triangle dans le système de
    // // coordonnées du VertexSet
    // if (isPointInTriangle(vertexSetX, vertexSetY, pa, pb, pc)) {
    // // Calculer la couleur en fonction des coordonnées et des couleurs des
    // sommets
    // Color color = calculateColor(vertexSetX, vertexSetY, pa, pb, pc, ca, cb, cc);
    // // Définir la couleur du pixel dans l'image
    // pixelWriter.setColor(x, y, color);
    // // pixelWriter.setColor(x, y, Color.BLUE);
    // } else {
    // pixelWriter.setColor(x, y, Color.BLACK);
    // }
    // }
    // }

    // // Utiliser l'image comme motif de remplissage
    // return new ImagePattern(image, 0, 0, 1, 1, true);
    // }

    // private boolean isPointInTriangle(double x, double y, Point pa, Point pb,
    // Point pc) {
    // // Vérifier si le point (x, y) est à l'intérieur du triangle en utilisant la
    // // méthode des barycentres
    // double d = (pb.getY() - pc.getY()) * (pa.getX() - pc.getX())
    // + (pc.getX() - pb.getX()) * (pa.getY() - pc.getY());
    // double alpha = ((pb.getY() - pc.getY()) * (x - pc.getX()) + (pc.getX() -
    // pb.getX()) * (y - pc.getY())) / d;
    // double beta = ((pc.getY() - pa.getY()) * (x - pc.getX()) + (pa.getX() -
    // pc.getX()) * (y - pc.getY())) / d;
    // double gamma = 1 - alpha - beta;
    // return alpha > 0 && beta > 0 && gamma > 0;
    // }

    // private Color calculateColor(double x, double y, Point pa, Point pb, Point
    // pc, Color ca, Color cb, Color cc) {
    // // Calculer les distances normalisées entre le point et chaque sommet du
    // // triangle
    // double distA = distance(x, y, pa.getX(), pa.getY()) / distance(pa.getX(),
    // pa.getY(), pb.getX(), pb.getY());
    // double distB = distance(x, y, pb.getX(), pb.getY()) / distance(pb.getX(),
    // pb.getY(), pc.getX(), pc.getY());
    // double distC = distance(x, y, pc.getX(), pc.getY()) / distance(pa.getX(),
    // pa.getY(), pc.getX(), pc.getY());

    // // Interpoler les couleurs en fonction des distances
    // double red = distA * ca.getRed() + distB * cb.getRed() + distC * cc.getRed();
    // double green = distA * ca.getGreen() + distB * cb.getGreen() + distC *
    // cc.getGreen();
    // double blue = distA * ca.getBlue() + distB * cb.getBlue() + distC *
    // cc.getBlue();

    // System.out.println(red + " :: "+blue+" :: "+ green);
    // if (red > 1.0 || blue > 1.0 || green>1.0) {
    // return Color.GREEN;
    // }

    // // Retourner la couleur interpolée
    // return Color.rgb((int) (255 * red), (int) (255 * green), (int) (255 * blue));
    // }

    // private double distance(double x1, double y1, double x2, double y2) {
    // return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    // }
}
