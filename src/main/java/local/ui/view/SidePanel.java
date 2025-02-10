package local.ui.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SidePanel extends VBox {

    public SidePanel() {
        super();
        Label averageUsageLabel = new Label("Average Usage: 0.0 ms/frame");

        setStyle("-fx-background-color: lightgray;");
        setPrefWidth(200);
        setAlignment(Pos.CENTER_LEFT);
        getChildren().add(averageUsageLabel);
    }
}
