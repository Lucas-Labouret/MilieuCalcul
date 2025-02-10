package local.ui.camera.keybindings;

import javafx.scene.input.KeyCode;
import local.ui.camera.HoldedKey;
import local.ui.camera.SimpleFPSCamera;

public class ForwardKB extends HoldedKey {

    private SimpleFPSCamera cam;

    public ForwardKB(SimpleFPSCamera cam) {
        super(KeyCode.Z);
        this.cam=cam;
    }

    @Override
    public void onHold() {
        cam.getAffine().setTx(cam.getPosition().getX() + cam.getMoveSpeed() * cam.getMoveNormal().getX());
        // affine.setTy(getPosition().getY() + moveSpeed * getN().getY());
        cam.getAffine().setTz(cam.getPosition().getZ() + cam.getMoveSpeed() * cam.getMoveNormal().getZ());
    }
}
