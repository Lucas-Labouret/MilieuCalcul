package local.ui.camera;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Callback;
import local.ui.Main;
import local.ui.camera.keybindings.BackwardKB;
import local.ui.camera.keybindings.DownwardKB;
import local.ui.camera.keybindings.ForwardKB;
import local.ui.camera.keybindings.LeftwardKB;
import local.ui.camera.keybindings.PrintPosition;
import local.ui.camera.keybindings.RightwardKB;
import local.ui.camera.keybindings.UpwardKB;

public class SimpleFPSCamera extends Parent {

    public SimpleFPSCamera() {
        initialize();
    }

    private void update() {
        updateControls();
    }

    private void updateControls() {
        for (ActionKey ak : this.actionKey) {
            if (ak.isActive()) {
                ak.onHold();
            }
        }
    }

    private final Group root = new Group();
    private final Affine affine = new Affine();
    private final Translate translation = new Translate(0, 0, 0);
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS),
            rotateY = new Rotate(0, Rotate.Y_AXIS),
            rotateZ = new Rotate(0, Rotate.Z_AXIS);

    private double moveSpeed = 10.0;
    private double mousePosX;
    private double mousePosY;

    private ArrayList<ActionKey> actionKey;

    private void initialize() {
        getChildren().add(root);
        getTransforms().add(affine);
        initializeCamera();
        startUpdateThread();

        this.actionKey = new ArrayList<>();
        actionKey.add(new PrintPosition(this));

        actionKey.add(new ForwardKB(this));
        actionKey.add(new LeftwardKB(this));
        actionKey.add(new BackwardKB(this));
        actionKey.add(new RightwardKB(this));
        actionKey.add(new UpwardKB(this));
        actionKey.add(new DownwardKB(this));

    }

    public void loadControlsForScene(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, ke -> {
            for (ActionKey ak : this.actionKey) {
                if (ak.isKey(ke.getCode())) {
                    ak.onPressed();
                }
            }
        });

        scene.addEventHandler(KeyEvent.KEY_RELEASED, ke -> {
            for (ActionKey ak : this.actionKey) {
                if (ak.isKey(ke.getCode())) {
                    ak.onReleased();
                }
            }
        });

        scene.addEventHandler(MouseEvent.MOUSE_MOVED, me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            translation.setX(getPosition().getX());
            translation.setY(getPosition().getY());
            translation.setZ(getPosition().getZ());
            affine.setToIdentity();
            double angleY = (mousePosX / Main.WIDTH) * 360;
            double angleX = clamp((0.95 - ((mousePosY + 0.05 * Main.HEIGHT) / Main.HEIGHT)), 0, 1) * 180 + 90;
            rotateX.setAngle(angleX);
            rotateY.setAngle(-angleY);
            affine.prepend(translation.createConcatenation(rotateY.createConcatenation(rotateX)));
        });

        scene.addEventHandler(ScrollEvent.SCROLL, se -> {
            double v = getCamera().getFieldOfView() + ((se.getDeltaY() > 0) ? -1 : +1);
            getCamera().setFieldOfView(v);
        });

    }

    public void loadControlsForSubScene(SubScene scene) {
        // sceneProperty().addListener(l -> {
        // if (getScene() != null) {
        System.out.println("Yo");
        // scene.setOnMouseClicked(event -> scene.requestFocus());

        scene.addEventHandler(KeyEvent.KEY_PRESSED, ke -> {
            for (ActionKey ak : this.actionKey) {
                if (ak.isKey(ke.getCode())) {
                    ak.onPressed();
                }
            }
        });

        // scene.addEventHandler(MouseEvent.ANY, e -> System.out.println("HALOOOO"));

        scene.addEventHandler(KeyEvent.KEY_RELEASED, ke -> {
            for (ActionKey ak : this.actionKey) {
                if (ak.isKey(ke.getCode())) {
                    ak.onReleased();
                }
            }
        });

        scene.addEventHandler(MouseEvent.MOUSE_MOVED, me -> {
            scene.requestFocus();
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            translation.setX(getPosition().getX());
            translation.setY(getPosition().getY());
            translation.setZ(getPosition().getZ());
            affine.setToIdentity();
            double angleY = (mousePosX / Main.WIDTH) * 360;
            double angleX = clamp((0.95 - ((mousePosY + 0.05 * Main.HEIGHT) / Main.HEIGHT)), 0, 1) * 180 + 90;
            rotateX.setAngle(angleX);
            rotateY.setAngle(-angleY);
            affine.prepend(translation.createConcatenation(rotateY.createConcatenation(rotateX)));
        });

        scene.addEventHandler(ScrollEvent.SCROLL, se -> {
            double v = getCamera().getFieldOfView() + ((se.getDeltaY() > 0) ? -1 : +1);
            getCamera().setFieldOfView(v);
        });

        scene.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                scene.requestFocus();
            }
        });
        // }
        // });
    }

    private void initializeCamera() {
        getCamera().setNearClip(0.1);
        getCamera().setFarClip(100000);
        getCamera().setFieldOfView(42);
        getCamera().setVerticalFieldOfView(true);
        root.getChildren().add(getCamera());
    }

    private void startUpdateThread() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        }.start();
    }

    private final ReadOnlyObjectWrapper<PerspectiveCamera> camera = new ReadOnlyObjectWrapper<>(this, "camera",
            new PerspectiveCamera(true));

    public final PerspectiveCamera getCamera() {
        return camera.get();
    }

    public ReadOnlyObjectProperty cameraProperty() {
        return camera.getReadOnlyProperty();
    }

    /*
     * Callbacks
     * | R | Up| F | | P|
     * U |mxx|mxy|mxz| |tx|
     * V |myx|myy|myz| |ty|
     * N |mzx|mzy|mzz| |tz|
     */
    // Forward / look direction
    private final Callback<Transform, Point3D> F = (a) -> {
        return new Point3D(a.getMzx(), a.getMzy(), a.getMzz());
    };
    private final Callback<Transform, Point3D> N = (a) -> {
        return new Point3D(a.getMxz(), a.getMyz(), a.getMzz());
    };
    // up direction
    private final Callback<Transform, Point3D> UP = (a) -> {
        return new Point3D(a.getMyx(), a.getMyy(), a.getMyz());
    };
    private final Callback<Transform, Point3D> V = (a) -> {
        return new Point3D(a.getMxy(), a.getMyy(), a.getMzy());
    };
    // right direction
    private final Callback<Transform, Point3D> R = (a) -> {
        return new Point3D(a.getMxx(), a.getMxy(), a.getMxz());
    };
    private final Callback<Transform, Point3D> U = (a) -> {
        return new Point3D(a.getMxx(), a.getMyx(), a.getMzx());
    };
    // position
    private final Callback<Transform, Point3D> P = (a) -> {
        return new Point3D(a.getTx(), a.getTy(), a.getTz());
    };

    private Point3D getF() {
        return F.call(getLocalToSceneTransform());
    }

    public Point3D getLookDirection() {
        return getF();
    }

    private Point3D getN() {
        return N.call(getLocalToSceneTransform());
    }

    public Point3D getLookNormal() {
        return getN();
    }

    public Point3D getMoveNormal() {
        Point3D direction = new Point3D(getLookNormal().getX(), 0, getLookNormal().getZ());
        return direction.normalize();
    }

    private Point3D getR() {
        return R.call(getLocalToSceneTransform());
    }

    public Point3D getU() {
        return U.call(getLocalToSceneTransform());
    }

    private Point3D getUp() {
        return UP.call(getLocalToSceneTransform());
    }

    public Point3D getV() {
        return V.call(getLocalToSceneTransform());
    }

    public final Point3D getPosition() {
        return P.call(getLocalToSceneTransform());
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public Affine getAffine() {
        return affine;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

}