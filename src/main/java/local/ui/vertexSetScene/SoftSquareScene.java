package local.ui.vertexSetScene;

import javafx.scene.control.*;
import local.furthestpointoptimization.model.miseEnBoite.Coord;
import local.furthestpointoptimization.model.miseEnBoite.MiseEnBoite;
import local.furthestpointoptimization.model.miseEnBoite.RoundedCoordMeb;
import local.furthestpointoptimization.model.vertexSets.Vertex;
import local.furthestpointoptimization.model.vertexSets.VertexSet;
import local.furthestpointoptimization.model.optimisation.FPOUtils;

import java.util.HashMap;

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
        vertexSet = new VertexSet(pointCount);
        showVertexSet();
    }
}
