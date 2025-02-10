package local.Ui;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import local.furthestpointoptimization.model.vertexSets.Vertex;
import local.furthestpointoptimization.model.vertexSets.VertexSet;
import local.furthestpointoptimization.model.optimisation.FPOUtils;

public class Triangulation2DView extends BorderPane {

    ToolBar toolbar;
    Canvas canvas;

    VBox pointCountBox;
    TextField pointCountField;

    ToggleGroup modeGroup;
    RadioButton randomOption, borderOption;
    HBox inputContainer;

    VBox widthBox, heightBox; // contains label + text
    TextField widthField, heightField;

    ComboBox<String> borderTypeBox, orientationBox;

    private VertexSet vertexSet;

    public Triangulation2DView() {
        super();
        toolbar = new ToolBar();

        // Radio Buttons for selection
        modeGroup = new ToggleGroup();
        randomOption = new RadioButton("Aléatoire");
        borderOption = new RadioButton("Bordure");
        randomOption.setToggleGroup(modeGroup);
        borderOption.setToggleGroup(modeGroup);
        randomOption.setSelected(true);

        // Input fields
        inputContainer = new HBox();
        inputContainer.setSpacing(10);
        inputContainer.setAlignment(Pos.CENTER);

        pointCountBox = new VBox();
        pointCountField = new TextField("20");
        pointCountField.setMaxWidth(40);
        pointCountBox.getChildren().addAll(new Label("Count"), pointCountField);
        inputContainer.getChildren().add(pointCountBox);

        widthBox = new VBox();
        widthBox.setAlignment(Pos.CENTER);
        widthField = new TextField("10");
        widthField.setPrefWidth(40);
        widthBox.getChildren().addAll(new Label("Width"), widthField);

        heightBox = new VBox();
        heightBox.setAlignment(Pos.CENTER);
        heightField = new TextField("12");
        heightField.setMaxWidth(40);
        heightBox.getChildren().addAll(new Label("Height"), heightField);

        borderTypeBox = new ComboBox<>();
        borderTypeBox.getItems().addAll("1:1", "1:Sqrt(2)", "Custom");
        borderTypeBox.getSelectionModel().selectFirst();
        borderTypeBox.valueProperty().addListener((obs, oldVal, newVal) -> updateInputFields());

        orientationBox = new ComboBox<>();
        orientationBox.getItems().addAll("Portrait", "Landscape");
        orientationBox.setMaxWidth(20);
        orientationBox.getSelectionModel().selectFirst();

        inputContainer.getChildren().add(borderTypeBox);

        // Generate button
        Button gen = new Button("Generate");
        gen.setOnAction((event) -> {
            int pointCount = Integer.parseInt(pointCountField.getText());
            if (randomOption.isSelected()) {
                vertexSet = new VertexSet(pointCount);
                showVertexSet();
            } else if (borderOption.isSelected()) {
                int width = Integer.parseInt(widthField.getText());
                int height = 0;
                String selectedBorderType = borderTypeBox.getValue();
                if ("1:1".equals(selectedBorderType)) {
                    height = width;
                } else if ("1:Sqrt(2)".equals(selectedBorderType)) {
                    height = (int) (width * Math.sqrt(2));
                    if ("Landscape".equals(orientationBox.getValue())) {
                        int tmp = width;
                        width = height;
                        height = tmp;
                    }
                } else if ("Custom".equals(selectedBorderType)) {
                    height = Integer.parseInt(heightField.getText());
                }
                vertexSet = VertexSet.newHexBorderedSet(width, height, pointCount);
                showVertexSet();
            }
        });

        Button triangulateButton = new Button("Triangulate");
        triangulateButton.setOnAction(event -> {
            if (vertexSet != null) {
                vertexSet.delaunayTriangulate();
            }
            showVertexSet();
        });

        Button fpo = new Button("FPO");
        fpo.setOnAction(event -> {
            if (vertexSet != null) {
                FPOUtils.fpoIteration(vertexSet);
            }
            showVertexSet();
        });

        toolbar.getItems().addAll(randomOption, borderOption, inputContainer, gen, triangulateButton, fpo);
        setTop(toolbar);

        canvas = new Canvas();

        // Update the canvas size when the size of the BorderPane changes
        widthProperty().addListener((obs, oldVal, newVal) -> updateCanvasSize());
        heightProperty().addListener((obs, oldVal, newVal) -> updateCanvasSize());

        this.borderOption.setOnAction(ev -> updateInputFields());
        this.randomOption.setOnAction(ev -> updateInputFields());

        // Update input fields based on initial selection
        updateInputFields();
        showVertexSet();
    }

    private void updateCanvasSize() {
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight() - toolbar.getHeight());
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (vertexSet != null) {
            showVertexSet();
        }
    }

    private void updateInputFields() {
        inputContainer.getChildren().clear();
        if (randomOption.isSelected()) {
            inputContainer.getChildren().add(pointCountBox);
        } else if (borderOption.isSelected()) {
            inputContainer.getChildren().add(borderTypeBox);
            String selectedBorderType = borderTypeBox.getValue();
            // inputContainer.getChildren().add(widthField);
            inputContainer.getChildren().add(widthBox);
            if ("Custom".equals(selectedBorderType)) {
                inputContainer.getChildren().add(heightBox);
            }

            if (!"Custom".equals(selectedBorderType) && !"1:1".equals(selectedBorderType)) {
                inputContainer.getChildren().add(orientationBox);
            }
            inputContainer.getChildren().add(pointCountBox);
        }
    }

    void showVertexSet() {
        if (vertexSet == null) {
            setCenter(new Label("Nothing to show"));
            return;
        }
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

        for (Vertex v : vertexSet) {
            double x = (v.getX() - xmin) * scale + offsetX;
            double y = (v.getY() - ymin) * scale + offsetY;
            if (v.isBorder()) {
                gc.setFill(javafx.scene.paint.Color.GREEN);
            } else {
                gc.setFill(javafx.scene.paint.Color.BLACK);
            }
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
