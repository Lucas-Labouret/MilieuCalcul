package local.ui.camera.keybindings;

import javafx.scene.input.KeyCode;
import local.ui.camera.ActionKey;
import local.ui.camera.SimpleFPSCamera;

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
