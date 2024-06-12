package local.Ui.camera.keybindings;

import javafx.scene.input.KeyCode;
import local.Ui.camera.HoldedKey;
import local.Ui.camera.SimpleFPSCamera;

public class RightwardKB extends HoldedKey {
    private SimpleFPSCamera cam;

    public RightwardKB(SimpleFPSCamera cam) {
        super(KeyCode.D);
        this.cam=cam;
    }

    @Override
    public void onHold() {
        cam.getAffine().setTx(cam.getPosition().getX() + cam.getMoveSpeed() * cam.getU().getX());
        cam.getAffine().setTz(cam.getPosition().getZ() + cam.getMoveSpeed() * cam.getU().getZ());
    }
}