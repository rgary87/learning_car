package fr.rgary.learningcar.base;

import fr.rgary.learningcar.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Class PrintUtil.
 */
public class PrintUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrintUtil.class);

    public static void logPopulation(List<Car> population) {
        String toPrint = "[";
        for (Car car : population) {
            toPrint += car.fitnessValue + ", ";
        }
        toPrint = toPrint.substring(0, toPrint.length() - 2);
        toPrint += "]";
        LOGGER.info(toPrint);
    }

    public static void logPopulationNumberAndFitness(List<Car> population) {
        String toPrint = "[";
        for (int i = 0; i < population.size() || i < 15; i++) {
            toPrint += population.get(i).number + ": " + population.get(i).fitnessValue + ", ";
        }
//        for (Car car : population) {
//            toPrint += car.number + ": " + car.fitnessValue + ", ";
//        }
        toPrint = toPrint.substring(0, toPrint.length() - 2);
        toPrint += "]";
        LOGGER.info(toPrint);
    }

    public static void logPopulationNumberAndFitnessAsTable(List<Car> population) {
        LOGGER.info("|   #   |  Fitness | Move | Zone | ");
        for (int i = 0; i < population.size() && i < 1; i++) {
            LOGGER.info(String.format("| %5d | %8.2f | %4d | %4d | ",
                    population.get(i).number,
                    population.get(i).fitnessValue,
                    population.get(i).moveDone,
                    population.get(i).maxZoneEntered
            ));
        }
    }
}
