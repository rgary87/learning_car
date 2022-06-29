package fr.rgary.learningcar;

import fr.rgary.learningcar.machinelearning.GeneticAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Processor.
 */
public class Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);
    public static Population POPULATION;
    private static int populationSize = 500;
    public static int toSelect = populationSize / 3 * 2;
    public Boolean activeCar = true;
    public int activeCarCount = 0;
//    private ExecutorService executorService = Executors.newFixedThreadPool(populationSize);
    public static int GENERATION = 0;

    public Processor() {
        if (populationSize < 10) {
            populationSize = 10;
        }
        Processor.POPULATION = new Population(populationSize);
        POPULATION.createCarList();
    }

    public void mainLogicalLoop() {
        if (activeCar) {
            Population.CARLIST.parallelStream().forEach(Car::run);
            this.updateActiveCar();
        } else {
            long start = System.nanoTime();
            GeneticAlgorithm.natureIsBeautiful();
            Population.CARLIST.parallelStream().forEach(Car::reset);
            activeCar = true;
//            LOGGER.warn("TOTAL TOOK {}ms. {}ms in breed. {}ms in mutate.", (System.nanoTime() - start) / 1_000_000f, GeneticAlgorithm.totalBreedTime / 1_000_000f, GeneticAlgorithm.totalMutateTime / 1_000_000f);
            GeneticAlgorithm.totalBreedTime = 0;
            GeneticAlgorithm.totalMutateTime = 0;
            GENERATION++;
        }
    }

    public void updateActiveCar() {
        this.activeCarCount = 0;
        for (Car car : Population.CARLIST) {
            if (car.isActive()) {
                this.activeCarCount++;
            }
        }
        this.activeCar = activeCarCount > 0;
    }

    public Population getPopulation() {
        return Processor.POPULATION;
    }


}
