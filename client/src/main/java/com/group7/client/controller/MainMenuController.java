package com.group7.client.controller;

import com.group7.client.definitions.screen.ScreenManager;
import com.group7.client.controller.common.BaseController;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/** Controller for the main menu*/
@Component
public class MainMenuController extends BaseController {
    /** Setter injection method*/
    @Autowired
    public void setManagers(@Lazy ScreenManager screenManager) {
        this.mScreenManager = screenManager;
    }

    /** Switches to the login form*/
    @FXML
    private void clickLoginButton() {
        mScreenManager.activatePane("login_form", null);
    }

    /** Switches to the register form*/
    @FXML
    private void clickRegisterButton() {
        mScreenManager.activatePane("register_form", null);
    }

    /** Switches to the reset password form*/
    @FXML
    public void clickForgotPassword() {
        mScreenManager.activatePane("reset_password_form", null);
    }
}
