package local.ui.utils;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class SidePanel extends VBox {
    public SidePanel() {
        setStyle("-fx-background-color: lightgray;");
        setSpacing(10);
        setAlignment(Pos.TOP_LEFT);
    }
}
