package local.ui.view;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** Toolbar int input */
public class TBIntInput extends VBox {

    private Label text;
    private TextField inputField;

    private TBIntInput() {
        super();
    }

    public TBIntInput(String name, String defaultText) {
        this.text = new Label(name);
        inputField = new TextField(defaultText);
        this.build();
    }

    public TBIntInput(String name) {
        new TBIntInput(name, "");
    }

    public static TBIntInput withoutName(String defaultText) {
        TBIntInput tbinput = new TBIntInput();
        tbinput.text = null;
        tbinput.inputField = new TextField(defaultText);
        tbinput.build();
        return tbinput;
    }

    public static TBIntInput withoutName() {
        TBIntInput tbinput = TBIntInput.withoutName("");
        tbinput.build();
        return tbinput;
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
