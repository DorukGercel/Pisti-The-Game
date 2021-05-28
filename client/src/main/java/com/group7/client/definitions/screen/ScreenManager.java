package com.group7.client.definitions.screen;

import com.group7.client.controller.GameTableController;
import com.group7.client.definitions.game.CardTable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for dealing with Scene and Scene Graph
 * Also event publishing for scenes occur here
 * */
@Component
public class ScreenManager {
    /** Holds the panes and the name of the parents of the application*/
    private final Map<String, Pair<Pane,String>> mPaneMap;
    /** Holds the scene of the application*/
    private Scene mCurrentScene;
    /** Holds the name of the parent of the current scene*/
    private String mParentSceneName;
    /** Application context created via Spring*/
    private final ApplicationContext mApplicationContext;

    // TODO: Delete this component
    private final CardTable mCardTable;

    /** Required args constructor*/
    public ScreenManager(ApplicationContext applicationContext,
                         CardTable cardTable,
                         @Value("${spring.application.screen.common.url}") String commonUrl,
                         @Value("${spring.application.screen.panes.name}") String[] panesNames,
                         @Value("${spring.application.screen.panes.url}") String[] panesUrls,
                         @Value("${spring.application.screen.panes.parent}") String[] parentNames) {
        this.mPaneMap = new HashMap<>();
        this.mApplicationContext = applicationContext;
        this.mCardTable = cardTable;
        initPaneMap(panesUrls, panesNames, parentNames);
        initScene(commonUrl);
    }

    /** Get the current scene*/
    public Scene getCurrentScene() {
        return mCurrentScene;
    }

    /**
     * Change the root of scene graph to switch screen.
     *
     * @param name of the scene to switch to.
     */
    public void activatePane(String name, ApplicationEvent event){
        if(event != null) {
            publishEvent(event);
        }
        // Main root pane is border pane
        BorderPane root = (BorderPane) mCurrentScene.getRoot();
        // Always change the center of the border
        root.setCenter(mPaneMap.get(name).getKey());

        if (hasParent(name)) {
            publishEvent(new BackButtonEvent(true));
        } else {
            publishEvent(new BackButtonEvent(false));
        }
    }

    /**
     * Change the current scene to the parent of this scene
     */
    public void returnParentScene() {
        // Event is null, no event in return back
        activatePane(mParentSceneName ,null);
    }

    /** Publish the given event*/
    public void publishEvent(ApplicationEvent event) {
        mApplicationContext.publishEvent(event);
    }

    /** Create the first scene of the application*/
    private void initScene(String commonUrl) {
        try {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(commonUrl);
            FXMLLoader fxmlLoader = new FXMLLoader(resource.getURL());
            fxmlLoader.setControllerFactory(mApplicationContext::getBean);
            // Main root pane is border pane
            BorderPane root = fxmlLoader.load();
            root.setCenter(mPaneMap.get("main_menu").getKey());
            mCurrentScene = new Scene(root);
            // TODO: Remove print
            System.out.println("common");

        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /** Loads all the panes that are used in the app*/
    private void initPaneMap(String[] panesUrls, String[] panesNames, String[] parentsNames) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource;
        for (int i = 0; i < panesUrls.length; i++) {
            resource = resourceLoader.getResource(panesUrls[i]);
            addPane(panesNames[i], resource, parentsNames[i]);
            // TODO: Remove print
            System.out.println(panesNames[i]);
            System.out.println(parentsNames[i] + "\n");
        }
    }

    /** Adds a pane to the pane map*/
    private void addPane(String paneName, Resource resource, String parentName){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(resource.getURL());
            fxmlLoader.setControllerFactory(mApplicationContext::getBean);
            Pane pane = fxmlLoader.load();
            mPaneMap.put(paneName, new Pair<>(pane, parentName));
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /** Removes the pane from the pane map*/
    private void removePane(String paneName){
        mPaneMap.remove(paneName);
    }

    /** Helper function which checks if pane has parent and sets parent scene name*/
    private boolean hasParent(String paneName) {
        mParentSceneName = mPaneMap.get(paneName).getValue();
        return !paneName.equals(mParentSceneName);
    }

    /** Event which indicates the game is started*/
    public static class BackButtonEvent extends ApplicationEvent {
        public BackButtonEvent(boolean code) {
            super(code);
        }
    }
}
