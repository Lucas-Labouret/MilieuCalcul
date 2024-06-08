package local.Ui.View;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

public class ToolBarComponent extends ToolBar {

    BackendButton back;
    public Button fortune;
    public Button thirdDimension;

    public ToolBarComponent() {
        super();
        back = new BackendButton();
        thirdDimension = new Button("3D");
        fortune = new Button("Fortune");
        getItems().add(back);
        getItems().add(thirdDimension);
        getItems().add(fortune);

        // Button btn1 = new Button("Backend");
        // Button btn3 = new Button("Button 3");
    }


    public BackendButton getBackendButton() {
        return back;
    }

}
