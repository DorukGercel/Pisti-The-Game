package com.group7.client;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<UiApplication.StageReadyEvent> {
    @Value("${spring.application.ui.windowWidth}") private int windowWidth;
    @Value("${spring.application.ui.windowHeight}") private int windowHeight;
    private final String applicationTitle;
    private final ScreenManager mScreenManager;

    public StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle,
                            ScreenManager screenManager) {
        this.applicationTitle = applicationTitle;
        this.mScreenManager = screenManager;
    }

    @Override
    public void onApplicationEvent(UiApplication.StageReadyEvent stageReadyEvent) {
        Stage stage = stageReadyEvent.getStage();
        stage.addEventFilter(WindowEvent.WINDOW_SHOWN, windowEvent -> {
        });
        stage.setScene(mScreenManager.getCurrentScene());
        stage.setTitle(applicationTitle);
        stage.setMinWidth(windowWidth);
        stage.setMinHeight(windowHeight);
        stage.setMaxWidth(windowWidth);
        stage.setMaxHeight(windowHeight);
        stage.show();
    }
}
