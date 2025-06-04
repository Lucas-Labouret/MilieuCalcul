package local.ui.utils;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import local.computingMedia.cannings.Canning;
import local.computingMedia.cannings.evaluation.MasksComputer;

/**
 * A utility class that displays the maximum and average values of various mask metrics.
 * It is designed to be used in a JavaFX application to show the results of mask computations.
 */
public class MaskLister extends GridPane {
    public MaskLister(MasksComputer masksComputer) {
        setAlignment(Pos.CENTER);
        setHgap(10);
        setVgap(10);

        Label max = new Label("Max");
        Label average = new Label("Average");

        Label veEv = new Label("Ve <-> Ev");
        Label maxVeEv = new Label(String.valueOf(masksComputer.getMaxVeEv()));
        Label averageVeEv = new Label(String.format("%.2f", masksComputer.getAverageVeEv()));

        Label vfFv = new Label("Vf <-> Fv");
        Label maxVfFv = new Label(String.valueOf(masksComputer.getMaxVfFv()));
        Label averageVfFv = new Label(String.format("%.2f", masksComputer.getAverageVfFv()));

        Label efFe = new Label("Ef <-> Fe");
        Label maxEfFe = new Label(String.valueOf(masksComputer.getMaxEfFe()));
        Label averageEfFe = new Label(String.format("%.2f", masksComputer.getAverageEfFe()));

        GridPane.setHalignment(max, HPos.CENTER);
        GridPane.setHalignment(average, HPos.CENTER);

        GridPane.setHalignment(veEv, HPos.LEFT);
        GridPane.setHalignment(vfFv, HPos.LEFT);
        GridPane.setHalignment(efFe, HPos.LEFT);

        GridPane.setHalignment(maxVeEv, HPos.RIGHT);
        GridPane.setHalignment(averageVeEv, HPos.RIGHT);

        GridPane.setHalignment(maxVfFv, HPos.RIGHT);
        GridPane.setHalignment(averageVfFv, HPos.RIGHT);

        GridPane.setHalignment(maxEfFe, HPos.RIGHT);
        GridPane.setHalignment(averageEfFe, HPos.RIGHT);

                               add(max    , 1, 0); add(average    , 2, 0);
        add(veEv, 0, 1); add(maxVeEv, 1, 1); add(averageVeEv, 2, 1);
        add(vfFv, 0, 2); add(maxVfFv, 1, 2); add(averageVfFv, 2, 2);
        add(efFe, 0, 3); add(maxEfFe, 1, 3); add(averageEfFe, 2, 3);
    }
}
