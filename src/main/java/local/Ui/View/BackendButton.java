package local.Ui.View;

// import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
// import javafx.scene.control.Label;

public class BackendButton extends Button {
    public BackendButton() {
        super("backend");
    }

}

//     private AnimationTimer timer;
//     private long frameCount = 0;
//     private long startTime = System.nanoTime();
//     private Label averageUsageLabel = new Label("Average Usage: 0.0 ms/frame");
 
//     public BackendButton() {
//         super("Backend");
//         setOnAction(event -> {
//             if (borderPane.getLeft() == null) {
//                 borderPane.setLeft(sidePanel);
//                 startTimer();
//             } else {
//                 borderPane.setLeft(null);
//                 stopTimer();
//             }
//         });
//     }

//     private void startTimer() {
//         if (timer == null) {
//             timer = new AnimationTimer() {
//                 @Override
//                 public void handle(long now) {
//                     frameCount++;
//                     long elapsedTime = now - startTime;
//                     double averageFrameTime = (double) elapsedTime / frameCount / 1_000_000.0;
//                     averageUsageLabel.setText(String.format("Average Usage: %.2f ms/frame", averageFrameTime));
//                 }
//             };
//         }
//         timer.start();
//     }

//     private void stopTimer() {
//         if (timer != null) {
//             timer.stop();
//             frameCount = 0;
//             startTime = System.nanoTime();
//         }
//     }
// }
