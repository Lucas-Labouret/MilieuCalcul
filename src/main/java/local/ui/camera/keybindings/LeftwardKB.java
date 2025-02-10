package local.ui.camera.keybindings;

import javafx.scene.input.KeyCode;
import local.ui.camera.HoldedKey;
import local.ui.camera.SimpleFPSCamera;

public class LeftwardKB extends HoldedKey {

    private SimpleFPSCamera cam;

    public LeftwardKB(SimpleFPSCamera cam) {
        super(KeyCode.Q);
        this.cam=cam;
    }

    @Override
    public void onHold() {
        cam.getAffine().setTx(cam.getPosition().getX() + cam.getMoveSpeed() * -cam.getU().getX());
        cam.getAffine().setTz(cam.getPosition().getZ() + cam.getMoveSpeed() * -cam.getU().getZ());
    }
}