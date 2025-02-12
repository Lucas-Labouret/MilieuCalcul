package local.ui.vertexSetScene;

import javafx.scene.control.*;
import local.computingMedium.miseEnBoite.MiseEnBoite;
import local.computingMedium.miseEnBoite.RoundedCoordMeb;
import local.computingMedium.vertexSets.SoftSquareSet;

public class SoftSquareScene extends VertexSetScene {
    public MiseEnBoite DEFAULT_MEB() { return new RoundedCoordMeb(); }

    TextField pointCountField;

    public SoftSquareScene() {
        pointCountField = new TextField("20");
        pointCountField.setMaxWidth(40);

        toolbar.getItems().addAll(pointCountField, gen, tri, fpo, fpoIterations, meb);
        setTop(toolbar);
    }

    public SoftSquareScene(MiseEnBoite miseEnBoite) {
        this();
        this.miseEnBoite = miseEnBoite;
    }

    protected void generate() {
        int pointCount = Integer.parseInt(pointCountField.getText());
        vertexSet = new SoftSquareSet(pointCount);
        showVertexSet();
    }
}
