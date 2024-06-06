package local.Ui.camera;

import javafx.scene.input.KeyCode;

public abstract class ActionKey {
    private KeyCode kc;
    private boolean pressed;
    
    public ActionKey(KeyCode kc) {
        this.kc=kc;
    }

    public KeyCode getKc() {
        return kc;
    }

    public boolean isKey(KeyCode kc) {
        return kc == this.kc;
    }

    public void activate() {
        pressed = true;
    }

    public void deactivate() {
        pressed = false;
    }

    public boolean isActive() {
        return pressed;
    }

    public abstract void onPressed();
    public abstract void onHold();
    public abstract void onReleased();
}
