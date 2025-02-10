package local.ui.camera.keybindings;

import javafx.scene.input.KeyCode;
import local.ui.camera.HoldedKey;
import local.ui.camera.SimpleFPSCamera;

public class DownwardKB extends HoldedKey {
    private SimpleFPSCamera cam;

    public DownwardKB(SimpleFPSCamera cam) {
        super(KeyCode.SHIFT);
        this.cam=cam;
    }

    @Override
    public void onHold() {
        cam.getAffine().setTy(cam.getPosition().getY() + cam.getMoveSpeed() * cam.getV().getY());
    }
}
