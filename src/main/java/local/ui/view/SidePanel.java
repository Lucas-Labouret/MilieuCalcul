package local.ui.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SidePanel extends VBox {

    public SidePanel() {
        super();
        setStyle("-fx-background-color: lightgray;");
        setPrefWidth(200);
        setAlignment(Pos.TOP_LEFT);
    }
}
