package local.ui.utils;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** Toolbar int input */
@SuppressWarnings("unused")
public class TBIntInput extends VBox {

    private Label text;
    private TextField inputField;

    private TBIntInput() {}

    public TBIntInput(String name, String defaultText) {
        this.text = new Label(name);
        inputField = new TextField(defaultText);
        this.build();
    }

    public TBIntInput(String name) {
        new TBIntInput(name, "");
    }

    private void build() {
        ObservableList<Node> c = this.getChildren();
        if (text != null) {
            c.add(this.text);
        }
        c.add(inputField);
        setMaxWidth(40);
        setAlignment(Pos.CENTER);
    }

    public int getValue() {
        return Integer.parseInt(this.inputField.getText());
    }

}
