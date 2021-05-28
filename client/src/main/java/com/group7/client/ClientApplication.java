package com.group7.client;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring application which is responsible for launching UI App
 */
@EnableAsync
@SpringBootApplication
public class ClientApplication {

	public static void main(String[] args) {
		Application.launch(UiApplication.class, args);
	}

}
