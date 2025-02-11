package local.ui.vertexSetScene;

import javafx.scene.control.*;
import local.furthestpointoptimization.model.miseEnBoite.MiseEnBoite;
import local.furthestpointoptimization.model.vertexSets.SoftSquareSet;
import local.furthestpointoptimization.model.vertexSets.VertexSet;

public class SoftSquareScene extends VertexSetScene {
    TextField pointCountField;

    public SoftSquareScene(MiseEnBoite miseEnBoite) {
        super(miseEnBoite);

        pointCountField = new TextField("20");
        pointCountField.setMaxWidth(40);

        toolbar.getItems().addAll(pointCountField, gen, tri, fpo, meb);
        setTop(toolbar);

        showVertexSet();
    }

    protected void generate() {
        int pointCount = Integer.parseInt(pointCountField.getText());
        vertexSet = new SoftSquareSet(pointCount);
        showVertexSet();
    }
}
