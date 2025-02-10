package local.ui.view;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class InformationBar extends Pane {

    private String defaultText;
    private Label text;

    public InformationBar() {
        this("");
    }

    public InformationBar(String defaultText) {
        this.defaultText = defaultText;
        build();
    }

    private void build() {
        this.text = new Label(this.defaultText);
        setStyle("-fx-background-color: #CCCCCC;");
        getChildren().add(this.text);
    }

    public void setLabel(String text) {
        this.text.setText(text);
    }

    public void setText(String text) {
        this.setLabel(text);
    }

    public void removeText() {
        setLabel(this.defaultText);
    }
}
