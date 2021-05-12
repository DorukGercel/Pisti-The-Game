package com.group7.client.controller;

import com.group7.client.ScreenManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class MainMenuController {

    private ScreenManager mScreenManager;

    @Autowired
    public void setScreenManager(@Lazy ScreenManager screenManager) {
        this.mScreenManager = screenManager;
    }

    @FXML
    public void clickLoginButton() {
        mScreenManager.activatePane("login_form");
    }

    @FXML
    public void clickRegisterButton() {
        mScreenManager.activatePane("register_form");
    }
}
