package fr.rgary.learningcar;

import fr.rgary.learningcar.display.Display;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LearningCarApplication {

	public static void main(String[] args) {
		Display display = new Display();
		display.run();
//		SpringApplication.run(LearningCarApplication.class, args);
	}

}
