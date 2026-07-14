package com.angeloni.nutricare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main Nutricare Desktop Application
 * Spring Boot + JavaFX integration
 * 
 * This application serves as both the backend (Spring Boot services, database access)
 * and the frontend (JavaFX UI) in a single compiled JAR.
 */
@SpringBootApplication(
	exclude = {
		DispatcherServletAutoConfiguration.class  // Disable web servlet (desktop app, not web app)
	}
)
@ComponentScan(basePackages = "com.angeloni.nutricare")
@EnableJpaRepositories(basePackages = "com.angeloni.nutricare.repository")
@EntityScan("com.angeloni.nutricare.entity")
public class NutricareApplication extends Application {
	
	private static String[] appArgs;
	private static org.springframework.context.ConfigurableApplicationContext springContext;

	public static void main(String[] args) {
		appArgs = args;
		Application.launch(NutricareApplication.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Start Spring Boot context in a separate thread
		new Thread(() -> {
			springContext = SpringApplication.run(NutricareApplication.class, appArgs);
		}, "Spring Boot Thread").start();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		// Shutdown Spring Boot context on application close
		if (springContext != null) {
			springContext.close();
		}
	}
}
