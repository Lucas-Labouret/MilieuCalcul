package local.ui.utils;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * A utility class that provides a side panel for displaying content in a JavaFX application.
 * It extends ScrollPane to allow scrolling through the content.
 */
public class SidePanel extends ScrollPane {
    private static final String BG_STYLE = "-fx-background-color: lightgray;";
    private final VBox content = new VBox();

    public SidePanel() {
        setStyle(BG_STYLE);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setFitToWidth(true);
        setFitToHeight(true);
        setContent(content);

        content.setStyle(BG_STYLE);
        content.setSpacing(10);
        content.setAlignment(Pos.TOP_LEFT);
    }

    public void add(Node node) {
        content.getChildren().add(node);
    }
}
