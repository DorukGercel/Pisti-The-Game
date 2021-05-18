package com.group7.client.controller;

import com.group7.client.definitions.screen.ScreenManager;
import com.group7.client.controller.common.BaseController;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/** Controller for the main menu*/
@Component
public class MainMenuController extends BaseController {

    /** Reference to common screen manager*/
    private ScreenManager mScreenManager;

    /** Setter injection method*/
    @Autowired
    public void setManagers(@Lazy ScreenManager screenManager) {
        this.mScreenManager = screenManager;
    }

    /** Switches to the login form*/
    @FXML
    public void clickLoginButton() {
        mScreenManager.activatePane("login_form");
    }

    /** Switches to the register form*/
    @FXML
    public void clickRegisterButton() {
        mScreenManager.activatePane("register_form");
    }
}
