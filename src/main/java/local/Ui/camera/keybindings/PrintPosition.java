package local.Ui.camera.keybindings;

import javafx.scene.input.KeyCode;
import local.Ui.camera.ActionKey;
import local.Ui.camera.SimpleFPSCamera;

public class PrintPosition extends ActionKey {
    SimpleFPSCamera cam;

    public PrintPosition(SimpleFPSCamera cam) {
        super(KeyCode.P);
        this.cam = cam;
    }

    @Override
    public void onPressed() {
        System.out.println("Position : " + cam.getPosition());
    }

    @Override
    public void onHold() {}
    @Override
    public void onReleased() {}
}
