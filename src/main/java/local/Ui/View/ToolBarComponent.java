package local.Ui.View;

import javafx.scene.control.ToolBar;

public class ToolBarComponent extends ToolBar {

    BackendButton back;

    public ToolBarComponent() {
        super();
        back = new BackendButton();
        getItems().add(back);

        // Button btn1 = new Button("Backend");
        // Button btn2 = new Button("Button 2");
        // Button btn3 = new Button("Button 3");
    }


    public BackendButton getBackendButton() {
        return back;
    }

}
