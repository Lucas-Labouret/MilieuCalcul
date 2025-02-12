package local.ui.vertexSetScene;

import javafx.scene.control.TextField;
import local.computingMedium.miseEnBoite.MiseEnBoite;
import local.computingMedium.miseEnBoite.RoundedCoordMeb;
import local.computingMedium.vertexSets.VertexSet;

public class SceneLoader extends VertexSetScene{
    public MiseEnBoite DEFAULT_MEB() { return new RoundedCoordMeb(); }

    TextField fileName;

    public SceneLoader() {
        fileName = new TextField();
        fileName.setPromptText("File Name");
        fileName.setMaxWidth(100);

        gen.setText("Load");
        topToolBar.getItems().addAll(fileName, gen, tri, fpo, fpoIterations, meb);
        setTop(topToolBar);
    }

    public SceneLoader(MiseEnBoite miseEnBoite) {
        this();
        this.miseEnBoite = miseEnBoite;
    }

    protected void generate() {
        vertexSet = VertexSet.fromFile("save/"+fileName.getText()+".vtxs");
        showVertexSet();
    }

}
