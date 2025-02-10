package local.ui.camera.keybindings;


import javafx.scene.input.KeyCode;
import local.ui.camera.HoldedKey;
import local.ui.camera.SimpleFPSCamera;

public class UpwardKB extends HoldedKey {
    private SimpleFPSCamera cam;

    public UpwardKB(SimpleFPSCamera cam) {
        super(KeyCode.SPACE);
        this.cam=cam;
    }

    @Override
    public void onHold() {
        cam.getAffine().setTy(cam.getPosition().getY() + cam.getMoveSpeed() * -cam.getV().getY());
    }
}