package local.ui.utils;

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
import javafx.scene.text.Text;
import local.computingMedia.cannings.Canning;
import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.*;
import local.computingMedia.tLoci.*;

public class MediumDrawer extends Pane {
    private static final String BG_STYLE = "-fx-background-color: #FFFFFF;";
    
    private static final int SLOCI_RADIUS = 5;
    private static final double TLOCI_SPACING = 0.33;

    private static final Color VERTEX_ISOLATED_COLOR = Color.BLACK;
    private static final Color VERTEX_LOW_COLOR = Color.BLUE;
    private static final Color VERTEX_HIGH_COLOR = Color.RED;
    private static final Color VERTEX_BORDER_COLOR = Color.GREEN;
    private static final Color EDGE_COLOR = Color.GOLD;
    private static final Color FACE_COLOR = Color.GREEN;

    private static final Color EF_COLOR = Color.LIGHTPINK;
    private static final Color FE_COLOR = Color.DEEPPINK;
    private static final Color EV_COLOR = Color.DEEPSKYBLUE;
    private static final Color VE_COLOR = Color.DARKBLUE;
    private static final Color FV_COLOR = Color.RED;
    private static final Color VF_COLOR = Color.ORANGE;

    private static final Color OFF_COLOR = Color.LIGHTGRAY;

    private static final Color COORD_COLOR = Color.BLACK;

    private boolean SHOW_VERTICES = true;
    private boolean SHOW_VERTICES_COORDS = false;
    private boolean SHOW_EDGES = true;
    private boolean SHOW_EDGES_COORDS = false;
    private boolean EDGES_AS_LINES = true;
    private boolean SHOW_FACES = false;
    private boolean SHOW_FACES_COORDS = false;

    private boolean SHOW_EF_FE = false;
    private boolean SHOW_EF_FE_COORDS = false;
    private boolean SHOW_EV_VE = false;
    private boolean SHOW_EV_VE_COORDS = false;
    private boolean SHOW_FV_VF = false;
    private boolean SHOW_FV_VF_COORDS = false;

    private boolean SHOW_CANNING = false;

    private boolean SHOW_TRANSFER_EF_FE = false;
    private boolean SHOW_TRANSFER_FE_EF = false;
    private boolean SHOW_TRANSFER_EV_VE = false;
    private boolean SHOW_TRANSFER_VE_EV = false;
    private boolean SHOW_TRANSFER_FV_VF = false;
    private boolean SHOW_TRANSFER_VF_FV = false;

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
    public void setShowVerticesCoords(boolean showVerticesCoords) {
        SHOW_VERTICES_COORDS = showVerticesCoords;
        redraw();
    }
    public void setShowEdges(boolean showEdges) {
        SHOW_EDGES = showEdges;
        redraw();
    }
    public void setShowEdgesCoords(boolean showEdgesCoords) {
        SHOW_EDGES_COORDS = showEdgesCoords;
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
    public void setShowFacesCoords(boolean showFacesCoords) {
        SHOW_FACES_COORDS = showFacesCoords;
        redraw();
    }

    public void setShowEfFe(boolean showEfFe) {
        SHOW_EF_FE = showEfFe;
        redraw();
    }
    public void setShowEfFeCoords(boolean showEfFeCoords) {
        SHOW_EF_FE_COORDS = showEfFeCoords;
        redraw();
    }
    public void setShowEvVe(boolean showEvVe) {
        SHOW_EV_VE = showEvVe;
        redraw();
    }
    public void setShowEvVeCoords(boolean showEvVeCoords) {
        SHOW_EV_VE_COORDS = showEvVeCoords;
        redraw();
    }
    public void setShowFvVf(boolean showFvVf) {
        SHOW_FV_VF = showFvVf;
        redraw();
    }
    public void setShowFvVfCoords(boolean showFvVfCoords) {
        SHOW_FV_VF_COORDS = showFvVfCoords;
        redraw();
    }

    public void setShowCanning(boolean showCanning) {
        SHOW_CANNING = showCanning;
        redraw();
    }

    public void setShowTransferEfFe(boolean showTransferEfFe) {
        SHOW_TRANSFER_EF_FE = showTransferEfFe;
        redraw();
    }
    public void setShowTransferFeEf(boolean showTransferFeEf) {
        SHOW_TRANSFER_FE_EF = showTransferFeEf;
        redraw();
    }
    public void setShowTransferEvVe(boolean showTransferEvVe) {
        SHOW_TRANSFER_EV_VE = showTransferEvVe;
        redraw();
    }
    public void setShowTransferVeEv(boolean showTransferVeEv) {
        SHOW_TRANSFER_VE_EV = showTransferVeEv;
        redraw();
    }
    public void setShowTransferFvVf(boolean showTransferFvVf) {
        SHOW_TRANSFER_FV_VF = showTransferFvVf;
        redraw();
    }
    public void setShowTransferVfFv(boolean showTransferVfFv) {
        SHOW_TRANSFER_VF_FV = showTransferVfFv;
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

        canningGrid = new Vertex[canningHeight][canningWidth];
        for (Vertex vertex : medium) {
            VertexCoord coord = canning.getVertexCanning().get(vertex);
            canningGrid[coord.Y()][coord.X()] = vertex;
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
        if (SHOW_TRANSFER_EF_FE          ) drawTransferEfFe();
        if (SHOW_TRANSFER_FE_EF          ) drawTransferFeEf();
        if (SHOW_TRANSFER_EV_VE          ) drawTransferEvVe();
        if (SHOW_TRANSFER_VE_EV          ) drawTransferVeEv();
        if (SHOW_TRANSFER_FV_VF          ) drawTransferFvVf();
        if (SHOW_TRANSFER_VF_FV          ) drawTransferVfFv();
        if (SHOW_CANNING                 ) drawCanning();
        if (SHOW_EF_FE                   ) drawEfFe();
        if (SHOW_EV_VE                   ) drawEvVe();
        if (SHOW_FV_VF                   ) drawFvVf();
        if (SHOW_VERTICES                ) drawVertices();
    }

    private Color getColorFromNeighborCount(int count) {
        if (count == 0)
            return VERTEX_ISOLATED_COLOR;
        int criticalCount = 6;
        double sigmoid = 1 / (1 + Math.exp(-(count - criticalCount)));
        return VERTEX_LOW_COLOR.interpolate(VERTEX_HIGH_COLOR, sigmoid);
    }
    private void drawVertices() {
        for (Vertex v : canning.getVertices()) {
            double x = (v.getX() - xmin) * scale + offsetX;
            double y = (v.getY() - ymin) * scale + offsetY;

            Circle circle = new Circle(x, y, SLOCI_RADIUS);
            Circle selectionCircle = new Circle(x, y, (int)(1.5*SLOCI_RADIUS)); // Circle the selected point
            selectionCircle.setFill(null);
            selectionCircle.setStroke(Color.YELLOW);
            selectionCircle.setStrokeWidth(4);
            int neighborCount = v.getNeighbors().size();

            if (medium.partOfBorder(v)) {
                circle.setFill(VERTEX_BORDER_COLOR);
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

            if (SHOW_VERTICES_COORDS) {
                Text coords = new Text(x-15, y-10, canning.getVertexCanning().get(v).toString());
                coords.setFill(COORD_COLOR);
                getChildren().add(coords);
            }
        }
    }
    private void drawLines() {
        for (Edge e : canning.getEdges()) {
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
                color1 = OFF_COLOR;
                color2 = OFF_COLOR;
            }
            Stop[] stops = new Stop[] {new Stop(0, color1), new Stop(1, color2)};
            LinearGradient linearGradient = new LinearGradient(
                    gradientStartX, gradientStartY, gradientEndX, gradientEndY, true, CycleMethod.NO_CYCLE, stops);
            line.setStroke(linearGradient);
            line.setStrokeWidth(2);

            getChildren().add(line);

            if (SHOW_EDGES_COORDS) {
                double xmid = (x1 + x2) / 2;
                double ymid = (y1 + y2) / 2;

                Text coords = new Text(xmid-25, ymid, canning.getEdgeCanning().get(e).toString());
                coords.setFill(COORD_COLOR);
                getChildren().add(coords);
            }
        }
    }
    private void drawEdges() {
        for (Edge e : canning.getEdges()) {
            Vertex center = e.getCenter();
            double x = (center.getX() - xmin) * scale + offsetX;
            double y = (center.getY() - ymin) * scale + offsetY;

            Rectangle rectangle = new Rectangle(
                    x-SLOCI_RADIUS,
                    y-SLOCI_RADIUS,
                    2*SLOCI_RADIUS,
                    2*SLOCI_RADIUS
            );
            rectangle.setFill((SHOW_CANNING && canning != null)? OFF_COLOR: EDGE_COLOR);

            getChildren().add(rectangle);

            if (SHOW_EDGES_COORDS) {
                Text coords = new Text(x-25, y-10, canning.getEdgeCanning().get(e).toString());
                coords.setFill(COORD_COLOR);
                getChildren().add(coords);
            }
        }
    }
    private void drawFaces() {
        for (Face f : canning.getFaces()) {
            Vertex center = f.getCentroid();
            double x = (center.getX() - xmin) * scale + offsetX;
            double y = (center.getY() - ymin) * scale + offsetY;

            Polygon triangle = new Polygon(
                    x, y - SLOCI_RADIUS,
                    x - SLOCI_RADIUS, y + SLOCI_RADIUS,
                    x + SLOCI_RADIUS, y + SLOCI_RADIUS
            );
            triangle.setFill((SHOW_CANNING && canning != null)? OFF_COLOR: FACE_COLOR);

            getChildren().add(triangle);

            if (SHOW_FACES_COORDS) {
                Text coords = new Text(x-25, y-10, canning.getFaceCanning().get(f).toString());
                coords.setFill(COORD_COLOR);
                getChildren().add(coords);
            }
        }
    }

    private void drawTLoci(Vertex v1, Vertex v2, Color color, String coordsString) {
        Vertex v = Edge.getWeightedCenter(TLOCI_SPACING, v1, v2);
        double x = (v.getX() - xmin) * scale + offsetX;
        double y = (v.getY() - ymin) * scale + offsetY;

        color = (SHOW_CANNING && canning != null)? OFF_COLOR: color;
        Circle circle = new Circle(x, y, SLOCI_RADIUS/2.);
        circle.setFill(color);
        getChildren().add(circle);

        if (coordsString != null) {
            Text coords = new Text(x-25, y-10, coordsString);
            coords.setFill(COORD_COLOR);
            getChildren().add(coords);
        }
    }
    private void drawTLoci(Vertex v1, Vertex v2, Color color) { drawTLoci(v1, v2, color, null); }
    private void drawEfFe() {
        for (Ef ef: canning.getEf()){
            Vertex v1 = ef.e().getCenter();
            Vertex v2 = ef.f().getCentroid();
            if (SHOW_EF_FE_COORDS) drawTLoci(v1, v2, EF_COLOR, canning.getEfCanning().get(ef).toString());
            else drawTLoci(v1, v2, EF_COLOR);
        }

        for (Fe fe : canning.getFe()){
            Vertex v1 = fe.f().getCentroid();
            Vertex v2 = fe.e().getCenter();
            if (SHOW_EF_FE_COORDS) drawTLoci(v1, v2, FE_COLOR, canning.getFeCanning().get(fe).toString());
            else drawTLoci(v1, v2, FE_COLOR);
        }
    }
    private void drawEvVe() {
        for (Ev ev : canning.getEv()){
            Vertex v1 = ev.e().getCenter();
            Vertex v2 = ev.v();
            if (SHOW_EV_VE_COORDS) drawTLoci(v1, v2, EV_COLOR, canning.getEvCanning().get(ev).toString());
            else drawTLoci(v1, v2, EV_COLOR);
        }

        for (Ve ve : canning.getVe()){
            Vertex v1 = ve.v();
            Vertex v2 = ve.e().getCenter();
            if (SHOW_EV_VE_COORDS) drawTLoci(v1, v2, VE_COLOR, canning.getVeCanning().get(ve).toString());
            else drawTLoci(v1, v2, VE_COLOR);
        }
    }
    private void drawFvVf() {
        for (Fv fv : canning.getFv()){
            Vertex v1 = fv.f().getCentroid();
            Vertex v2 = fv.v();
            if (SHOW_FV_VF_COORDS) drawTLoci(v1, v2, FV_COLOR, canning.getFvCanning().get(fv).toString());
            else drawTLoci(v1, v2, FV_COLOR);
        }

        for (Vf vf : canning.getVf()){
            Vertex v1 = vf.v();
            Vertex v2 = vf.f().getCentroid();
            if (SHOW_FV_VF_COORDS) drawTLoci(v1, v2, VF_COLOR, canning.getVfCanning().get(vf).toString());
            else drawTLoci(v1, v2, VF_COLOR);
        }
    }

    private void drawTransfer(Vertex start, Vertex end, Color color1, Color color2) {
        double xStart = (start.getX() - xmin) * scale + offsetX;
        double yStart = (start.getY() - ymin) * scale + offsetY;
        double xEnd = (end.getX() - xmin) * scale + offsetX;
        double yEnd = (end.getY() - ymin) * scale + offsetY;
        Line line = new Line(xStart, yStart, xEnd, yEnd);

        color1 = (SHOW_CANNING && canning != null)? OFF_COLOR: color1;
        color2 = (SHOW_CANNING && canning != null)? OFF_COLOR: color2;
        double gradientStartX = xStart / paneWidth;
        double gradientStartY = yStart / paneHeight;
        double gradientEndX = xEnd / paneWidth;
        double gradientEndY = yEnd / paneHeight;
        Stop[] stops = new Stop[] {new Stop(0, color1), new Stop(1, color2)};
        LinearGradient gradient = new LinearGradient(
                gradientStartX, gradientStartY, gradientEndX, gradientEndY, true, CycleMethod.NO_CYCLE, stops);
        line.setStroke(gradient);
        line.setStrokeWidth(1);

        getChildren().add(line);
    }
    private void drawTransferEfFe(){
        for (Ef ef : canning.getEf()){
            Vertex v1 = ef.e().getCenter();
            Vertex v2 = ef.f().getCentroid();
            Vertex start = Edge.getWeightedCenter(TLOCI_SPACING, v1, v2);

            Fe fe = canning.getEfFeCommunication().get(ef);
            Vertex v3 = fe.f().getCentroid();
            Vertex v4 = fe.e().getCenter();
            Vertex end = Edge.getWeightedCenter(TLOCI_SPACING, v3, v4);

            drawTransfer(start, end, EF_COLOR, FE_COLOR);
        }
    }
    private void drawTransferFeEf(){
        for (Fe fe : canning.getFe()){
            Vertex v1 = fe.f().getCentroid();
            Vertex v2 = fe.e().getCenter();
            Vertex start = Edge.getWeightedCenter(TLOCI_SPACING, v1, v2);

            Ef ef = canning.getFeEfCommunication().get(fe);
            Vertex v3 = ef.e().getCenter();
            Vertex v4 = fe.f().getCentroid();
            Vertex end = Edge.getWeightedCenter(TLOCI_SPACING, v3, v4);

            drawTransfer(start, end, FE_COLOR, EF_COLOR);
        }
    }
    private void drawTransferEvVe(){
        for (Ev ev : canning.getEv()){
            Vertex v1 = ev.e().getCenter();
            Vertex v2 = ev.v();
            Vertex start = Edge.getWeightedCenter(TLOCI_SPACING, v1, v2);

            Ve ve = canning.getEvVeCommunication().get(ev);
            Vertex v3 = ve.v();
            Vertex v4 = ev.e().getCenter();
            Vertex end = Edge.getWeightedCenter(TLOCI_SPACING, v3, v4);

            drawTransfer(start, end, EV_COLOR, VE_COLOR);
        }
    }
    private void drawTransferVeEv(){
        for (Ve ve : canning.getVe()){
            Vertex v1 = ve.v();
            Vertex v2 = ve.e().getCenter();
            Vertex start = Edge.getWeightedCenter(TLOCI_SPACING, v1, v2);

            Ev ev = canning.getVeEvCommunication().get(ve);
            Vertex v3 = ev.e().getCenter();
            Vertex v4 = ev.v();
            Vertex end = Edge.getWeightedCenter(TLOCI_SPACING, v3, v4);

            drawTransfer(start, end, VE_COLOR, EV_COLOR);
        }
    }
    private void drawTransferFvVf(){
        for (Fv fv : canning.getFv()){
            Vertex v1 = fv.f().getCentroid();
            Vertex v2 = fv.v();
            Vertex start = Edge.getWeightedCenter(TLOCI_SPACING, v1, v2);

            Vf vf = canning.getFvVfCommunication().get(fv);
            Vertex v3 = vf.v();
            Vertex v4 = vf.f().getCentroid();
            Vertex end = Edge.getWeightedCenter(TLOCI_SPACING, v3, v4);
            drawTransfer(start, end, FV_COLOR, VF_COLOR);
        }
    }
    private void drawTransferVfFv(){
        for (Vf vf : canning.getVf()){
            Vertex v1 = vf.v();
            Vertex v2 = vf.f().getCentroid();
            Vertex start = Edge.getWeightedCenter(TLOCI_SPACING, v1, v2);

            Fv fv = canning.getVfFvCommunication().get(vf);
            Vertex v3 = fv.f().getCentroid();
            Vertex v4 = fv.v();
            Vertex end = Edge.getWeightedCenter(TLOCI_SPACING, v3, v4);

            drawTransfer(start, end, VF_COLOR, FV_COLOR);
        }
    }

    private void drawCanning() {
        if (canning == null) return;

        for (int j = 0; j < canningHeight; j++) {
            int i1 = 0;
            int i2 = 0;

            Vertex v1 = canningGrid[j][i1];
            while (v1 == null && i1 < canningWidth-1) {
                i1++;
                v1 = canningGrid[j][i1];
            }

            i2 = i1 + 1;
            Vertex v2;
            if (i2 < canningWidth) v2 = canningGrid[j][i2];
            else continue;
            while (v2 == null && i2 < canningWidth-1) {
                i2++;
                v2 = canningGrid[j][i2];
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
                    v2 = canningGrid[j][i2];
                } while (v2 == null && i2 < canningWidth-1);

            } while (i1 < canningWidth && i2 < canningWidth && v2 != null);
        }

        for (int i = 0; i < canningWidth; i++) {
            int j1 = 0;
            int j2 = 0;

            Vertex v1 = canningGrid[j1][i];
            while (v1 == null && j1 < canningHeight-1) {
                j1++;
                v1 = canningGrid[j1][i];
            }

            j2 = j1 + 1;
            Vertex v2;
            if (j2 < canningHeight) v2 = canningGrid[j2][i];
            else continue;
            while (v2 == null && j2 < canningHeight-1) {
                j2++;
                v2 = canningGrid[j2][i];
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
                    v2 = canningGrid[j2][i];
                } while (v2 == null && j2 < canningHeight-1);

            } while (j1 < canningHeight && j2 < canningHeight && v2 != null);
        }
    }
}
