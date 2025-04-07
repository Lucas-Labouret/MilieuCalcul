package local.ui.utils;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class InformationBar extends Pane {
    private final String BG_STYLE = "-fx-background-color: #f0f0f0;";

    private final String defaultText;
    private final Label text;

    public InformationBar() { this(""); }

    public InformationBar(String defaultText) {
        setStyle(BG_STYLE);
        this.defaultText = " " + defaultText + " ";
        this.text = new Label(this.defaultText);
        getChildren().add(this.text);
    }

    public void setText(String newText) { text.setText(" " + newText + " "); }
    public void removeText() { text.setText(this.defaultText); }
}
