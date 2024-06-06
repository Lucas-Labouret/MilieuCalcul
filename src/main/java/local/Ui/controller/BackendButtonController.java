package local.Ui.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import local.Ui.View.MainLayout;
import local.Ui.View.SidePanel;

public class BackendButtonController implements EventHandler<ActionEvent>  {
    MainLayout main;
    SidePanel sidepannel;
    public BackendButtonController(MainLayout main, SidePanel sidepannel) {
        this.main = main;
        this.sidepannel=sidepannel;

    }

    @Override
    public void handle(ActionEvent event) {
        // if (main.getLeft() == null) {
        //     main.setLeft(sidepannel);
        // } else {
        //     main.setLeft(null);
        // }

    };
}
