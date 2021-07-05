package com.group7.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Main FX application which is responsible for starting UI App
 */
public class UiApplication extends Application {

    /** Context of the running application*/
    private ConfigurableApplicationContext applicationContext;

    /** Initializes the application*/
    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(ClientApplication.class).run();
    }

    /** Creates an event to create stage*/
    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    /** Closes application context in exit*/
    @Override
    public void stop() {
        // Close context
        applicationContext.close();
        Platform.exit();
        // Clean all threads if any left
        System.exit(0);
    }

    /** Event which indicates the app is ready to create stage*/
    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return ((Stage) getSource());
        }
    }
}
