package local.Ui.camera;

import javafx.scene.input.KeyCode;

public abstract class HoldedKey extends ActionKey {

    public HoldedKey(KeyCode kc) {
        super(kc);
    }

    @Override
    public void onPressed() {
        activate();
    }

    @Override
    public void onReleased() {
        deactivate();
    }

    @Override
    public abstract void onHold();
}
