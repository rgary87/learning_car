package fr.rgary.learningcar;

import fr.rgary.learningcar.machinelearning.GeneticAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class Processor.
 */
public class Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);
    public static List<Car> POPULATION;
    private static int populationSize = 80;
    public static int toSelect = populationSize / 3 * 2;
    public Boolean activeCar = true;
    public int activeCarCount = 0;
    private ExecutorService executorService = Executors.newFixedThreadPool(populationSize);
    public static int GENERATION = 0;

    public Processor() {
        Processor.POPULATION = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            Processor.POPULATION.add(new Car());
        }
    }

    public void mainLogicalLoop() {
        if (activeCar) {
            Processor.POPULATION.parallelStream().filter(Car::isActive).forEach(Car::getSensorsValues);
            Processor.POPULATION.parallelStream().filter(Car::isActive).forEach(Car::moveMe);
            this.updateActiveCar();
        } else {
            long start = System.nanoTime();
            GeneticAlgorithm.natureIsBeautiful();
            POPULATION.parallelStream().forEach(Car::reset);
            activeCar = true;
            LOGGER.warn("TOTAL TOOK {}ms. {}ms in breed. {}ms in mutate.", (System.nanoTime() - start) / 1_000_000f, GeneticAlgorithm.totalBreedTime / 1_000_000f, GeneticAlgorithm.totalMutateTime / 1_000_000f);
            GeneticAlgorithm.totalBreedTime = 0;
            GeneticAlgorithm.totalMutateTime = 0;
            GENERATION++;
        }

    }

    public void updateActiveCar() {
        boolean b = false;
        this.activeCarCount = 0;
        for (Car car : POPULATION) {
            if (car.isActive()) {
                b = true;
                this.activeCarCount++;
            }
        }
        this.activeCar = b;
    }

    public List<Car> getPopulation() {
        return Processor.POPULATION;
    }

}
