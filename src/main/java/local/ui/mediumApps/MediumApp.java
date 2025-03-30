package local.ui.mediumApps;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import local.computingMedia.cannings.Canning;
import local.computingMedia.cannings.VertexCanningCompleter;
import local.computingMedia.cannings.vertexCannings.RoundedCoordVCanning;
import local.computingMedia.cannings.vertexCannings.TopDistanceXSortedLinesVCanning;
import local.computingMedia.media.Medium;
import local.ui.utils.InformationBar;
import local.ui.utils.MediumDrawer;
import local.savefileManagers.SavefileManager;
import local.ui.utils.SidePanel;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class MediumApp extends BorderPane {
    public abstract Canning DEFAULT_CANNING();

    protected final ToolBar topToolBar;
    protected final ToolBar botToolBar;
    protected final SidePanel sidePanel;
    protected final InformationBar infoBar;
    protected final MediumDrawer drawPane;

    protected Medium medium;
    protected Canning canning;

    protected final Button gen;
    protected final Button tri;
    protected final Button fpo;

    protected final InformationBar savefileInfo;
    protected SavefileManager savefileManager;
    protected final TextField fileName;
    protected final Button saveButton;
    protected final Button loadButton;

    protected boolean fpoToConvergence = false;
    protected int fpoIterations = 1;
    protected double convergenceTolerance = 0.9;

    public MediumApp() {
        canning = DEFAULT_CANNING();

        topToolBar = new ToolBar();
        botToolBar = new ToolBar();
        sidePanel = new SidePanel();
        infoBar = new InformationBar("Information");
        drawPane = new MediumDrawer(medium, canning);

        setTop(topToolBar);
        setCenter(drawPane);
        setRight(sidePanel);
        setBottom(botToolBar);

        gen = new Button("Generate");
        gen.setOnAction((event) -> generate());
        tri = new Button("Triangulate");
        tri.setOnAction(event -> triangulate());
        fpo = new Button("FPO");
        fpo.setOnAction(event -> fpoIteration());

        fillSidePanel();

        savefileInfo = new InformationBar();
        fileName = new TextField();
        saveButton = new Button("Save");
        saveButton.setOnAction(event -> save());
        loadButton = new Button("Load");
        loadButton.setOnAction(event -> load());
        botToolBar.getItems().addAll(fileName, saveButton, loadButton, savefileInfo);

        widthProperty().addListener((obs, oldVal, newVal) -> updateDrawPaneSize());
        heightProperty().addListener((obs, oldVal, newVal) -> updateDrawPaneSize());
        updateDrawPaneSize();
    }

    abstract protected void generate();

    protected void triangulate() {
        if (medium != null) {
            medium.delaunayTriangulate();
        }
        drawPane.redraw();
    }

    public void setFpoToConvergence(boolean fpoToConvergence) {
        this.fpoToConvergence = fpoToConvergence;
    }
    public void setFpoIterations(int fpoIterations) {
        this.fpoIterations = fpoIterations;
    }
    public void setConvergenceTolerance(double convergenceTolerance) {
        this.convergenceTolerance = convergenceTolerance;
    }

    protected void fpoIteration() {
        if (medium == null) return;

        if (fpoToConvergence) medium.optimizeToConvergence(convergenceTolerance);
        else medium.optimizeToSetIterations(fpoIterations);
        drawPane.redraw();
    }

    private void save(){
        if (fileName.getText().isEmpty()){
            savefileInfo.setText("Please enter a file name");
            return;
        }
        try { savefileManager.save(medium, getFileName()); }
        catch (IOException e) {
            savefileInfo.setText("Failed to save to a file.");
            e.printStackTrace();
        }
    }
    private void load(){
        try {
            medium = savefileManager.load(getFileName());
            canning.setMedium(medium);
            drawPane.setMedium(medium);
        }
        catch (FileNotFoundException e) { savefileInfo.setText("File not found."); }
        catch (IOException e) {
            savefileInfo.setText("File exists but couldn't be read.");
            e.printStackTrace();
        }
    }

    public void setCanning(Canning canning) {
        this.canning = canning;
        this.canning.setMedium(medium);
        drawPane.setCanning(canning);
    }

    public String getFileName() { return fileName.getText(); }

    private void updateDrawPaneSize() {
        drawPane.setPrefWidth(getWidth());
        drawPane.setPrefHeight(getHeight() - topToolBar.getHeight() - botToolBar.getHeight());
        drawPane.redraw();
    }

    private void fillSidePanel() {
        //Graphics
        CheckBox showVertices = new CheckBox("Show Vertices");
        CheckBox showEdges = new CheckBox("Show Edges");
        CheckBox edgesAsLines = new CheckBox("as lines");
        CheckBox showFaces = new CheckBox("Show Faces");

        CheckBox showCanning = new CheckBox("Show Canning");
        CheckBox showEfFe = new CheckBox("Show Ef/Fe");
        CheckBox showEvVe = new CheckBox("Show Ev/Ve");
        CheckBox showFvVf = new CheckBox("Show Fv/Vf");

        showVertices.setSelected(true);
        showEdges.setSelected(true);
        edgesAsLines.setSelected(true);
        showFaces.setSelected(false);

        showCanning.setSelected(false);
        showEfFe.setSelected(false);
        showEvVe.setSelected(false);
        showFvVf.setSelected(false);

        showVertices.allowIndeterminateProperty().set(false);
        showEdges.allowIndeterminateProperty().set(false);
        edgesAsLines.allowIndeterminateProperty().set(false);
        showFaces.allowIndeterminateProperty().set(false);

        showCanning.allowIndeterminateProperty().set(false);
        showEfFe.allowIndeterminateProperty().set(false);
        showEvVe.allowIndeterminateProperty().set(false);
        showFvVf.allowIndeterminateProperty().set(false);

        showVertices.selectedProperty().addListener((obs, oldVal, newVal) -> {
            drawPane.setShowVertices(newVal);
        });
        showEdges.selectedProperty().addListener((obs, oldVal, newVal) -> {
            drawPane.setShowEdges(newVal);
        });
        edgesAsLines.selectedProperty().addListener((obs, oldVal, newVal) -> {
            drawPane.setEdgesAsLines(newVal);
        });
        showFaces.selectedProperty().addListener((obs, oldVal, newVal) -> {
            drawPane.setShowFaces(newVal);
        });

        showCanning.selectedProperty().addListener((obs, oldVal, newVal) -> {
            drawPane.setShowCanning(newVal);
        });
        showEfFe.selectedProperty().addListener((obs, oldVal, newVal) -> {
            drawPane.setShowEfFe(newVal);
        });
        showEvVe.selectedProperty().addListener((obs, oldVal, newVal) -> {
            drawPane.setShowEvVe(newVal);
        });
        showFvVf.selectedProperty().addListener((obs, oldVal, newVal) -> {
            drawPane.setShowFvVf(newVal);
        });

        //Canning
        ToggleGroup canGroup = new ToggleGroup();

        RadioButton defaultCanning = new RadioButton("Default");
        RadioButton roundedCoordCanning = new RadioButton("Rounded Coordinates");
        RadioButton topDistanceXSortedCanning = new RadioButton("Top Distance X Sorted");

        defaultCanning.setToggleGroup(canGroup);
        roundedCoordCanning.setToggleGroup(canGroup);
        topDistanceXSortedCanning.setToggleGroup(canGroup);

        defaultCanning.setSelected(true);

        canGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == defaultCanning) {
                setCanning(DEFAULT_CANNING());
            } else if (newVal == roundedCoordCanning) {
                setCanning(new VertexCanningCompleter(new RoundedCoordVCanning()));
            } else if (newVal == topDistanceXSortedCanning) {
                setCanning(new VertexCanningCompleter(new TopDistanceXSortedLinesVCanning()));
            }
        });

        //FPO
        ToggleGroup fpoGroup = new ToggleGroup();

        RadioButton setIter = new RadioButton("Set iterations");
        TextField iterInput = new TextField("1");

        RadioButton toConvergence = new RadioButton("To convergence");
        TextField convInput = new TextField("0.9");

        setIter.setToggleGroup(fpoGroup);
        toConvergence.setToggleGroup(fpoGroup);

        setIter.setSelected(true);

        fpoGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            setFpoToConvergence(newVal == toConvergence);
        });

        iterInput.textProperty().addListener((obs, oldVal, newVal) -> {
            int iter;
            try { iter = Integer.parseInt(newVal); }
            catch (NumberFormatException e) { return; }
            setFpoIterations(iter);
        });

        convInput.textProperty().addListener((obs, oldVal, newVal) -> {
            double conv;
            try { conv = Double.parseDouble(newVal); }
            catch (NumberFormatException e) { return; }
            setConvergenceTolerance(conv);
        });


        //Add to side panel
        sidePanel.getChildren().addAll(
                new Label("Graphics"),
                showVertices,
                showEdges,
                new HBox(new Text("    ("), edgesAsLines, new Text(")")),
                showFaces,
                showCanning,
                showEfFe,
                showEvVe,
                showFvVf
        );

        sidePanel.getChildren().addAll(
                new Separator(),
                new Label("Canning"),
                defaultCanning,
                roundedCoordCanning,
                topDistanceXSortedCanning
        );

        sidePanel.getChildren().addAll(
                new Separator(),
                new Label("FPO"),
                setIter,
                iterInput,
                toConvergence,
                convInput
        );
    }
}
