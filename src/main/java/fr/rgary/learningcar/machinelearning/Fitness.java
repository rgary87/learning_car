package fr.rgary.learningcar.machinelearning;

import fr.rgary.learningcar.Car;
import fr.rgary.learningcar.Processor;
import fr.rgary.learningcar.base.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Fitness.
 */
public class Fitness {
    private static final Logger LOGGER = LoggerFactory.getLogger(Fitness.class);

    public static void calcFitness(Car car) {
//        throw new InternalError("NOT YET IMPLEMENTED");
        int carInZone = Constant.TRACK.getZoneNumberPerPosition(car.position);
        if (carInZone < car.maxZoneEntered || carInZone == -1) {
            LOGGER.warn("U-turns are BAD. You were in zone {} but returned to {}", car.maxZoneEntered, carInZone);
            car.active = false;
            car.fitnessValue = -1;
            car.maxZoneEntered = carInZone;
            return;
        }
        car.maxZoneEntered = carInZone;
        car.fitnessValue = Math.max(0, carInZone * 100) - (car.moveDone / 10f) + 450;

    }

    public static float getTotalFitness() {
        float total = 0;
        for (Car car : Processor.POPULATION) {
            total += car.fitnessValue;
        }
        return total;
    }
}
