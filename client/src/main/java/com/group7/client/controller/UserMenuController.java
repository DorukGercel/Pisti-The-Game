package com.group7.client.controller;

import com.group7.client.ScreenManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UserMenuController {

    private ScreenManager mScreenManager;

    @Autowired
    public void setScreenManager(@Lazy ScreenManager screenManager) {
        this.mScreenManager = screenManager;
    }

    @FXML
    public void clickStartGameButton() {
    }

    @FXML
    public void clickLeaderboardButton() {
        mScreenManager.activatePane("leaderboard");
    }

    @FXML
    public void clickLogoutButton() {
    }
}
