package local.ui.mediumApps;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import local.computingMedia.cannings.Canning;
import local.computingMedia.cannings.evaluation.MasksComputer;
import local.computingMedia.cannings.VertexCanningCompleter;
import local.computingMedia.cannings.simulatedAnnealing.VertexCanningNearestNeighborAnnealer;
import local.computingMedia.cannings.vertexCannings.RoundedCoordDichotomyVCanning;
import local.computingMedia.cannings.vertexCannings.RoundedCoordIncrementalVCanning;
import local.computingMedia.cannings.vertexCannings.TopDistanceXSortedLinesVCanning;
import local.computingMedia.cannings.vertexCannings.VertexCanningAnnealer;
import local.computingMedia.media.Medium;
import local.ui.utils.InformationBar;
import local.ui.utils.MaskLister;
import local.ui.utils.MediumDrawer;
import local.savefileManagers.SavefileManager;
import local.ui.utils.SidePanel;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * MediumApp is an abstract class that serves as a base for different medium applications.
 * A MediumApp draws different visualizations for {@link Medium} and  {@link Canning} and provides tools to manipulate them through a GUI.
 */
public abstract class MediumApp extends BorderPane {
    /** A canning factory interface that creates a {@link Canning} instance for a given {@link Medium}. */
    @FunctionalInterface
    protected interface CanningFactory {
        Canning make(Medium medium);
    }

    /**
     * Returns the default canning for this MediumApp.
     * This method should be overridden in subclasses to provide a specific default canning.
     * @return An instance of a default canning for this MediumApp.
     */
    public abstract Canning DEFAULT_CANNING();

    // UI components
    protected final ToolBar topToolBar;
    protected final ToolBar botToolBar;
    protected final SidePanel sidePanel;
    protected final InformationBar mediumInfoBar;
    protected final InformationBar maskInfoBar;
    protected final MediumDrawer drawPane;

    protected final VBox botVBox;

    protected final Button gen;
    protected final Button tri;
    protected final Button fpo;
    protected final Button msk;

    protected final InformationBar savefileInfo;
    protected final TextField fileName;
    protected final Button saveButton;
    protected final Button loadButton;

    // Model components
    protected Medium medium;
    protected Canning canning;
    protected CanningFactory canningFactory;
    protected MasksComputer masksComputer;
    protected SavefileManager savefileManager;

    // User provided parameters for FPO
    protected boolean fpoToConvergence = false;
    protected int fpoIterations = 1;
    protected double convergenceTolerance = 0.9;

    public MediumApp() {
        canningFactory = m -> DEFAULT_CANNING();
        masksComputer = new MasksComputer(canning);

        topToolBar = new ToolBar();
        sidePanel = new SidePanel();
        mediumInfoBar = new InformationBar();
        maskInfoBar = new InformationBar();
        botToolBar = new ToolBar();
        drawPane = new MediumDrawer();

        botVBox = new VBox();
        botVBox.getChildren().addAll(mediumInfoBar, maskInfoBar, botToolBar);

        setTop(topToolBar);
        setCenter(drawPane);
        setRight(sidePanel);
        setBottom(botVBox);

        gen = new Button("Generate");
        gen.setOnAction((event) -> generate());
        tri = new Button("Triangulate");
        tri.setOnAction(event -> triangulate());
        fpo = new Button("FPO");
        fpo.setOnAction(event -> applyFPO());
        msk = new Button("Masks");
        msk.setOnAction(event -> showMasks());

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

    /**
     * Generates the medium and canning for this MediumApp.
     * This method should be overridden in subclasses to provide generation logic specific to each type of medium.
     */
    abstract protected void generate();

    /** Common actions to be performed during generation. */
    protected void generateCommon(){
        setCanning();
        mediumInfoBar.removeText();
        maskInfoBar.removeText();
    }

    protected void triangulate() {
        if (medium != null) medium.delaunayTriangulate();
        canning.can();
        drawPane.redraw();
        updateInfoBars();
    }

    public void setFpoToConvergence(boolean fpoToConvergence) { this.fpoToConvergence = fpoToConvergence; }
    public void setFpoIterations(int fpoIterations) { this.fpoIterations = fpoIterations; }
    public void setConvergenceTolerance(double convergenceTolerance) { this.convergenceTolerance = convergenceTolerance; }

    protected void applyFPO() {
        if (medium == null) return;

        if (fpoToConvergence) medium.optimizeToConvergence(convergenceTolerance);
        else medium.optimizeToSetIterations(fpoIterations);
        canning.can();
        drawPane.redraw();
        updateInfoBars();
    }

    /** Creates a pop up window to display the masks' information. */
    private void showMasks() {
        Stage stage = new Stage();
        stage.initOwner(getScene().getWindow());
        stage.setTitle("Masks");

        MaskLister maskLister = new MaskLister(masksComputer);
        Scene scene = new Scene(maskLister);

        stage.setScene(scene);
        stage.show();
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
            setCanning();
            updateInfoBars();
        }
        catch (FileNotFoundException e) { savefileInfo.setText("File not found."); }
        catch (IOException e) {
            savefileInfo.setText("File exists but couldn't be read.");
            e.printStackTrace();
        }
    }

    public void setCanning() {
        canning = canningFactory.make(medium);
        canning.can();
        drawPane.setCanning(canning);
        masksComputer.setCanning(canning);
    }

    public String getFileName() { return fileName.getText(); }

    private void updateInfoBars() {
        if (medium == null) return;

        String mediumText = "Neighbors : max=" + medium.getMaxNeighborsCount() +
                ", min=" + medium.getInsideMinNeighborsCount() + "(" +medium.getMinNeighborsCount() + ")";
        mediumInfoBar.setText(mediumText);

        int[] data = masksComputer.getDeltas();
        int deltaY = data[0];
        int deltaX = data[1];
        int upperBound = data[2];
        String maskText = "Canning : delta Y=" + deltaY +
                          ", delta X=" + deltaX +
                          ", covered area=" + upperBound +
                          ", density=" + canning.getDensity();


        maskInfoBar.setText(maskText);
    }

    private void updateDrawPaneSize() {
        drawPane.setPrefWidth(getWidth() - sidePanel.getWidth());
        drawPane.setPrefHeight(getHeight() - topToolBar.getHeight() - botVBox.getHeight());
        drawPane.redraw();
    }

    private HBox subCheckBox(CheckBox checkBox) {
        return new HBox(new Label("    ("), checkBox, new Label(")"));
    }

    /**
     * Fills the side panel with various controls for the MediumApp.
     * This includes checkboxes for displaying different elements of the medium,
     * options for canning, and controls for FPO.
     */
    private void fillSidePanel() {
        //Graphic options
        CheckBox showVertices = new CheckBox("Vertices");
        CheckBox showVerticesCoords = new CheckBox("coordinates");
        CheckBox showEdges = new CheckBox("Edges");
        CheckBox edgesAsLines = new CheckBox("as lines");
        CheckBox showEdgesCoords = new CheckBox("coordinates");
        CheckBox showFaces = new CheckBox("Faces");
        CheckBox showFacesCoords = new CheckBox("coordinates");

        CheckBox showEfFe = new CheckBox("Ef/Fe");
        CheckBox showEfFeCoords = new CheckBox("coordinates");
        CheckBox showEvVe = new CheckBox("Ev/Ve");
        CheckBox showEvVeCoords = new CheckBox("coordinates");
        CheckBox showFvVf = new CheckBox("Fv/Vf");
        CheckBox showFvVfCoords = new CheckBox("coordinates");

        CheckBox showCanning = new CheckBox("Canning");
        CheckBox showCanningGrid = new CheckBox("Grid");
        CheckBox showCanningGridHoles = new CheckBox("Holes");

        CheckBox transferEfFe = new CheckBox("Ef -> Fe");
        CheckBox transferFeEf = new CheckBox("Fe -> Ef");
        CheckBox transferEvVe = new CheckBox("Ev -> Ve");
        CheckBox transferVeEv = new CheckBox("Ve -> Ev");
        CheckBox transferFvVf = new CheckBox("Fv -> Vf");
        CheckBox transferVfFv = new CheckBox("Vf -> Fv");

        showVertices.setSelected(true);
        showVerticesCoords.setSelected(false);
        showEdges.setSelected(true);
        edgesAsLines.setSelected(true);
        showEdgesCoords.setSelected(false);
        showFaces.setSelected(false);
        showFacesCoords.setSelected(false);

        showEfFe.setSelected(false);
        showEfFeCoords.setSelected(false);
        showEvVe.setSelected(false);
        showEvVeCoords.setSelected(false);
        showFvVf.setSelected(false);
        showFvVfCoords.setSelected(false);

        showCanning.setSelected(false);
        showCanningGrid.setSelected(false);
        showCanningGridHoles.setSelected(true);

        transferEfFe.setSelected(false);
        transferFeEf.setSelected(false);
        transferEvVe.setSelected(false);
        transferVeEv.setSelected(false);
        transferFvVf.setSelected(false);
        transferVfFv.setSelected(false);

        showVertices.allowIndeterminateProperty().set(false);
        showVerticesCoords.allowIndeterminateProperty().set(false);
        showEdges.allowIndeterminateProperty().set(false);
        edgesAsLines.allowIndeterminateProperty().set(false);
        showEdgesCoords.allowIndeterminateProperty().set(false);
        showFaces.allowIndeterminateProperty().set(false);
        showFacesCoords.allowIndeterminateProperty().set(false);

        showEfFe.allowIndeterminateProperty().set(false);
        showEfFeCoords.allowIndeterminateProperty().set(false);
        showEvVe.allowIndeterminateProperty().set(false);
        showEvVeCoords.allowIndeterminateProperty().set(false);
        showFvVf.allowIndeterminateProperty().set(false);
        showFvVfCoords.allowIndeterminateProperty().set(false);

        showCanning.allowIndeterminateProperty().set(false);
        showCanningGrid.allowIndeterminateProperty().set(false);
        showCanningGridHoles.allowIndeterminateProperty().set(false);

        transferEfFe.allowIndeterminateProperty().set(false);
        transferFeEf.allowIndeterminateProperty().set(false);
        transferEvVe.allowIndeterminateProperty().set(false);
        transferVeEv.allowIndeterminateProperty().set(false);
        transferFvVf.allowIndeterminateProperty().set(false);
        transferVfFv.allowIndeterminateProperty().set(false);

        showVertices.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowVertices(newVal));
        showVerticesCoords.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowVerticesCoords(newVal));
        showEdges.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowEdges(newVal));
        edgesAsLines.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setEdgesAsLines(newVal));
        showEdgesCoords.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowEdgesCoords(newVal));
        showFaces.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowFaces(newVal));
        showFacesCoords.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowFacesCoords(newVal));

        showEfFe.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowEfFe(newVal));
        showEfFeCoords.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowEfFeCoords(newVal));
        showEvVe.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowEvVe(newVal));
        showEvVeCoords.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowEvVeCoords(newVal));
        showFvVf.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowFvVf(newVal));
        showFvVfCoords.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowFvVfCoords(newVal));

        showCanning.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowCanning(newVal));
        showCanningGrid.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowCanningGrid(newVal));
        showCanningGridHoles.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowCanningGridHoles(newVal));

        transferEfFe.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowTransferEfFe(newVal));
        transferFeEf.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowTransferFeEf(newVal));
        transferEvVe.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowTransferEvVe(newVal));
        transferVeEv.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowTransferVeEv(newVal));
        transferFvVf.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowTransferFvVf(newVal));
        transferVfFv.selectedProperty().addListener((obs, oldVal, newVal) -> drawPane.setShowTransferVfFv(newVal));

        //Canning options
        ToggleGroup canGroup = new ToggleGroup();

        RadioButton defaultCanning = new RadioButton("Default");
        RadioButton roundedCoordIncrementalCanning = new RadioButton("Rounded Coordinates Incremental");
        RadioButton roundedCoordDichotomyCanning = new RadioButton("Rounded Coordinates Dichotomy");
        RadioButton topDistanceXSortedCanning = new RadioButton("Top Distance X Sorted");
        RadioButton AnnealedRoundedCoordCanning = new RadioButton("Annealed Rounded Coordinates Dichotomy");

        defaultCanning.setToggleGroup(canGroup);
        roundedCoordIncrementalCanning.setToggleGroup(canGroup);
        roundedCoordDichotomyCanning.setToggleGroup(canGroup);
        topDistanceXSortedCanning.setToggleGroup(canGroup);
        AnnealedRoundedCoordCanning.setToggleGroup(canGroup);

        defaultCanning.setSelected(true);


        canGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            // Modify the canning factory to always use the user selected canning
            if (newVal == defaultCanning) {
                canningFactory = m -> DEFAULT_CANNING();
            } else if (newVal == roundedCoordIncrementalCanning) {
                canningFactory = m -> new VertexCanningCompleter(new RoundedCoordIncrementalVCanning(m));
            } else if (newVal == roundedCoordDichotomyCanning) {
                canningFactory = m -> new VertexCanningCompleter(new RoundedCoordDichotomyVCanning(m));
            } else if (newVal == topDistanceXSortedCanning) {
                canningFactory = m -> new VertexCanningCompleter(new TopDistanceXSortedLinesVCanning(m));
            } else if (newVal == AnnealedRoundedCoordCanning) {
                canningFactory = m -> new VertexCanningCompleter(new VertexCanningAnnealer(
                        new RoundedCoordDichotomyVCanning(m),
                        new VertexCanningNearestNeighborAnnealer(500)
                ));
            }
            setCanning();
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

        fpoGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> setFpoToConvergence(newVal == toConvergence));

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
                subCheckBox(showVerticesCoords),
                showEdges,
                subCheckBox(edgesAsLines),
                subCheckBox(showEdgesCoords),
                showFaces,
                subCheckBox(showFacesCoords),
                showEfFe,
                subCheckBox(showEfFeCoords),
                showEvVe,
                subCheckBox(showEvVeCoords),
                showFvVf,
                subCheckBox(showFvVfCoords),
                new Label("Canning"),
                showCanning,
                showCanningGrid,
                subCheckBox(showCanningGridHoles),
                new Label("Transfers"),
                transferEfFe,
                transferFeEf,
                transferEvVe,
                transferVeEv,
                transferFvVf,
                transferVfFv
        );
        graphics.setSpacing(5);
        sidePanel.add(new TitledPane("Graphics", graphics));

        VBox canning = new VBox(
                defaultCanning,
                roundedCoordIncrementalCanning,
                roundedCoordDichotomyCanning,
                topDistanceXSortedCanning,
                AnnealedRoundedCoordCanning
        );
        canning.setSpacing(5);
        sidePanel.add(new TitledPane("Canning", canning));

        VBox fpo = new VBox(
                setIter,
                iterInput,
                toConvergence,
                convInput
        );
        fpo.setSpacing(5);
        sidePanel.add(new TitledPane("FPO", fpo));
    }
}
