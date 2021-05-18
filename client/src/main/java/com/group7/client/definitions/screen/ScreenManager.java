package com.group7.client.definitions.screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for dealing with Scene and Scene Graph
 * */
@Component
public class ScreenManager {
    /** Holds the panes of the application*/
    private final Map<String, Pane> mPaneMap;
    /** Holds the scene of the application*/
    private Scene currentScene;
    /** Application context created via Spring*/
    private final ApplicationContext applicationContext;

    /** Required args constructor*/
    public ScreenManager(ApplicationContext applicationContext,
                         @Value("${spring.application.screen.common.url}") String commonUrl,
                         @Value("${spring.application.screen.panes.name}") String[] panesName,
                         @Value("${spring.application.screen.panes.url}") String[] panesUrl) {
        mPaneMap = new HashMap<>();
        this.applicationContext = applicationContext;
        initPaneMap(panesUrl, panesName);
        initScene(commonUrl);
    }

    /** Get the current scene*/
    public Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * Change the root of scene graph to switch screen.
     *
     * @param name of the scene to switch to.
     */
    public void activatePane(String name){
        Pane root = (Pane) currentScene.getRoot();
        root.getChildren().setAll(mPaneMap.get(name));
    }

    /** Create the first scene of the application*/
    private void initScene(String commonUrl) {
        try {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(commonUrl);
            FXMLLoader fxmlLoader = new FXMLLoader(resource.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Pane pane = fxmlLoader.load();
            pane.getChildren().add(mPaneMap.get("main_menu"));
            currentScene = new Scene(pane);
            System.out.println("common");
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /** Loads all the panes that are used in the app*/
    private void initPaneMap(String[] panesUrl, String[] panesName) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource;
        for (int i = 0; i < panesUrl.length; i++) {
            resource = resourceLoader.getResource(panesUrl[i]);
            addPane(panesName[i], resource);
            System.out.println(panesName[i]);
        }
    }

    /** Adds a pane to the pane map*/
    private void addPane(String paneName, Resource resource){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(resource.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Pane pane = fxmlLoader.load();
            mPaneMap.put(paneName, pane);
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /** Removes the pane from the pane map*/
    private void removePane(String paneName){
        mPaneMap.remove(paneName);
    }

}
