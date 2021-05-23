package com.group7.client.controller;

import com.group7.client.controller.common.BaseController;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.definitions.player.PlayerManager;
import com.group7.client.definitions.screen.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/** Controller for the main background*/
@Component
public class CommonSceneController extends BaseController
        implements ApplicationListener<ScreenManager.BackButtonEvent>{

    private boolean buttonOpen = false;
    @FXML private Button back_button;

    /** Setter injection method*/
    @Autowired
    public void setManagers(@Lazy ScreenManager screenManager, NetworkManager networkManager, PlayerManager playerManager) {
        this.mScreenManager = screenManager;
        this.mPlayerManager = playerManager;
    }

    /** Init method of the controller*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        back_button.setVisible(false);
        buttonOpen = false;
    }

    /** Action listener method, sets the visibility of the back button*/
    @Override
    public void onApplicationEvent(ScreenManager.BackButtonEvent backButtonEvent) {
        if(!buttonOpen && backButtonEvent.getSource().equals(true)) {
            back_button.setVisible(true);
            buttonOpen = true;
        } else if(buttonOpen && backButtonEvent.getSource().equals(false)) {
            back_button.setVisible(false);
            buttonOpen = false;
        }
    }

    /** Return to the previous scene method*/
    @FXML
    public void clickBackButton() {
        mScreenManager.returnParentScene();
    }
}
