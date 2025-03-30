package local.ui.mediumApps;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

        CheckBox transferEfFe = new CheckBox("Ef -> Fe");
        CheckBox transferFeEf = new CheckBox("Fe -> Ef");
        CheckBox transferEvVe = new CheckBox("Ev -> Ve");
        CheckBox transferVeEv = new CheckBox("Ve -> Ev");
        CheckBox transferFvVf = new CheckBox("Fv -> Vf");
        CheckBox transferVfFv = new CheckBox("Vf -> Fv");

        showVertices.setSelected(true);
        showEdges.setSelected(true);
        edgesAsLines.setSelected(true);
        showFaces.setSelected(false);

        showCanning.setSelected(false);
        showEfFe.setSelected(false);
        showEvVe.setSelected(false);
        showFvVf.setSelected(false);

        transferEfFe.setSelected(false);
        transferFeEf.setSelected(false);
        transferEvVe.setSelected(false);
        transferVeEv.setSelected(false);
        transferFvVf.setSelected(false);
        transferVfFv.setSelected(false);

        showVertices.allowIndeterminateProperty().set(false);
        showEdges.allowIndeterminateProperty().set(false);
        edgesAsLines.allowIndeterminateProperty().set(false);
        showFaces.allowIndeterminateProperty().set(false);

        showCanning.allowIndeterminateProperty().set(false);
        showEfFe.allowIndeterminateProperty().set(false);
        showEvVe.allowIndeterminateProperty().set(false);
        showFvVf.allowIndeterminateProperty().set(false);

        transferEfFe.allowIndeterminateProperty().set(false);
        transferFeEf.allowIndeterminateProperty().set(false);
        transferEvVe.allowIndeterminateProperty().set(false);
        transferVeEv.allowIndeterminateProperty().set(false);
        transferFvVf.allowIndeterminateProperty().set(false);
        transferVfFv.allowIndeterminateProperty().set(false);

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

        transferEfFe.selectedProperty().addListener((obs, oldVal, newVal) -> {
            drawPane.setShowTransferEfFe(newVal);
        });
        transferFeEf.selectedProperty().addListener((obs, oldVal, newVal) -> {
            drawPane.setShowTransferFeEf(newVal);
        });
        transferEvVe.selectedProperty().addListener((obs, oldVal, newVal) -> {
            drawPane.setShowTransferEvVe(newVal);
        });
        transferVeEv.selectedProperty().addListener((obs, oldVal, newVal) -> {
            drawPane.setShowTransferVeEv(newVal);
        });
        transferFvVf.selectedProperty().addListener((obs, oldVal, newVal) -> {
            drawPane.setShowTransferFvVf(newVal);
        });
        transferVfFv.selectedProperty().addListener((obs, oldVal, newVal) -> {
            drawPane.setShowTransferVfFv(newVal);
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
        VBox graphics = new VBox(
                new Label("Loci"),
                showVertices,
                showEdges,
                new HBox(new Label("    ("), edgesAsLines, new Label(")")),
                showFaces,
                showCanning,
                showEfFe,
                showEvVe,
                showFvVf,
                new Label("Transfers"),
                transferEfFe,
                transferFeEf,
                transferEvVe,
                transferVeEv,
                transferFvVf,
                transferVfFv
        );
        graphics.setSpacing(5);
        sidePanel.getChildren().add(new TitledPane("Graphics", graphics));

        VBox canning = new VBox(
                defaultCanning,
                roundedCoordCanning,
                topDistanceXSortedCanning
        );
        canning.setSpacing(5);
        sidePanel.getChildren().add(new TitledPane("Canning", canning));

        VBox fpo = new VBox(
                setIter,
                iterInput,
                toConvergence,
                convInput
        );
        fpo.setSpacing(5);
        sidePanel.getChildren().add(new TitledPane("FPO", fpo));
    }
}
