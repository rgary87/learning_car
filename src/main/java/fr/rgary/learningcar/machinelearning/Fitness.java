package fr.rgary.learningcar.machinelearning;

import fr.rgary.learningcar.Car;
import fr.rgary.learningcar.Population;
import fr.rgary.learningcar.Processor;
import fr.rgary.learningcar.base.Constant;
import fr.rgary.learningcar.display.Display;
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
        carInZone = ((Constant.TRACK.zones.size()) * car.laps) + carInZone;
        if (carInZone < car.maxZoneEntered || carInZone == -1) {
            if (car.maxZoneEntered % (Constant.TRACK.zones.size() - 1) == 0) {
                car.laps++;
                carInZone = ((Constant.TRACK.zones.size()) * car.laps) + carInZone;
            } else {
//                LOGGER.warn("U-turns are BAD. You were in zone {} but returned to {}", car.maxZoneEntered, carInZone);
                if (car.maxZoneEntered == 142) {
                    Display.saveBestCar("./src/main/resources/hit_71.json", car);
                }
                car.active = false;
                car.fitnessValue = 0;
                car.maxZoneEntered = carInZone;
                return;
            }
        }
        car.maxZoneEntered = carInZone;
        car.fitnessValue = Math.max(0, car.maxZoneEntered * 100) - (car.moveDone / 10f) + 450;
        Processor.POPULATION.updateBest(car);
    }

    public static void adjustFitnessOnDifference(Car car, Car best, float maximumDifference) {
        float difference = car.getDifference(best);
        car.fitnessValue = car.fitnessValue * (difference / maximumDifference);
    }

    public static float getTotalFitness() {
        float total = 0;
        for (Car car : Population.CARLIST) {
            total += car.fitnessValue;
        }
        return total;
    }
}
