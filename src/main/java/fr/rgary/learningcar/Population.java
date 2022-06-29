package fr.rgary.learningcar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Population {
    public static List<Car> CARLIST;
    public static int size;
    public static Car best;
    public static float highiestFitness = 0;
    public static int selected = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(Population.class);

    public Population(int size) {
        Population.size = size;
        this.createCarList();
    }

    public void updateBest(Car car) {
        if (car.fitnessValue > highiestFitness) {
            LOGGER.info("New best car with fitness {}", car.fitnessValue);
            Population.best = car;
            Population.highiestFitness = car.fitnessValue;
        }
    }

    public void add(Car car) {
        CARLIST.add(car);
    }

    public static Car getAvailableCar() {
        for (Car car : CARLIST) {
            if (!car.selected) {
                car.selected = true;
                return car;
            }
        }
        return null;
    }

    public static Car getBest() {
        return Population.best;
    }

    public void createCarList() {
        CARLIST = new ArrayList<>(Population.size);
        for (int i = 0; i < Population.size; i++) {
            CARLIST.add(new Car(i));
        }
    }
}
