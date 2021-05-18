package com.group7.client;

import com.group7.client.definitions.screen.ScreenManager;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Component responsible for initializing stage and first scene
 * */
@Component
public class StageInitializer implements ApplicationListener<UiApplication.StageReadyEvent> {
    /** Width of stage*/
    @Value("${spring.application.ui.windowWidth}") private int windowWidth;
    /** Height of stage*/
    @Value("${spring.application.ui.windowHeight}") private int windowHeight;
    /** Title of stage*/
    private final String applicationTitle;
    /** Reference to common screen manager*/
    private final ScreenManager mScreenManager;

    /** Required args constructor*/
    public StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle,
                            ScreenManager screenManager) {
        this.applicationTitle = applicationTitle;
        this.mScreenManager = screenManager;
    }

    /** Stage Ready Event listener, creates stage*/
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
