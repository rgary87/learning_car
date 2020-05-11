package fr.rgary.learningcar.machinelearning;

import fr.rgary.learningcar.Car;
import fr.rgary.learningcar.Processor;
import fr.rgary.learningcar.base.PrintUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fr.rgary.learningcar.Processor.POPULATION;
import static fr.rgary.learningcar.base.Constant.MUTATE_CHANGE;
import static fr.rgary.learningcar.base.Constant.MUTATE_INDIVIDUAL_CHANGE;
import static fr.rgary.learningcar.base.Constant.RANDOM;

/**
 * Class GeneticAlgorithm.
 */
public class GeneticAlgorithm {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneticAlgorithm.class);

    public static long totalBreedTime = 0;
    public static long totalMutateTime = 0;

    public static float prevBestFit = -1;

    private static Car best;

    public GeneticAlgorithm() {
//        this.polygons = Track.instance.zones;
    }

    public static void natureIsBeautiful() {
        POPULATION.parallelStream().forEach(Fitness::calcFitness);
        Collections.sort(POPULATION);
        PrintUtil.logPopulationNumberAndFitnessAsTable(POPULATION);
        best = POPULATION.get(0);
        if (best.fitnessValue < prevBestFit) {
            LOGGER.info("What ? ");
        }
        prevBestFit = best.fitnessValue;
        List<Car> localPopulation = new ArrayList<>();
        selection(localPopulation);
        mutateAll(localPopulation);
        localPopulation.add(0, best);
        POPULATION = new ArrayList<>(localPopulation);
        POPULATION.forEach(Car::reset);
    }

    private static Car selectBasedOnFitness(float fitnessTotal) {
        double rand = RANDOM.nextDouble() * fitnessTotal;
        double runningSum = 0;
        for (Car car : POPULATION) {
            runningSum += car.fitnessValue;
            if (runningSum > rand) {
                return car;
            }
        }
        throw new InternalError("I HAVE NOTHING TO DO HERE !!!!");
    }

    private static Car breedChild(Car c1, Car c2) {
        long start = System.nanoTime();
        Car child = new Car();
        for (int i = 0; i < c1.allThetas.size(); i++) {
            for (int j = 0; j < c1.allThetas.get(i).data.length; j++) {
                child.allThetas.get(i).data[j] = RANDOM.nextDouble() < 0.5 ? c1.allThetas.get(i).data[j] : c2.allThetas.get(i).data[j];
            }
        }
        totalBreedTime += (System.nanoTime() - start);
        return child;
    }

    private static List<Car> selection(List<Car> localPopulation) {
        float fitnessTotal = Fitness.getTotalFitness();
        while (localPopulation.size() < Processor.toSelect) {
            localPopulation.add(new Car(selectBasedOnFitness(fitnessTotal)));
        }
        while (localPopulation.size() < POPULATION.size() - 1) {
            int i = RANDOM.nextInt(POPULATION.size());
            int j = RANDOM.nextInt(POPULATION.size());
            while (j == i) {
                j = RANDOM.nextInt(POPULATION.size());
            }
            Car firstParent = POPULATION.get(i);
            Car secondParent = POPULATION.get(j);

            Car child = breedChild(firstParent, secondParent);
            localPopulation.add(child);
        }
        return localPopulation;
    }

    private static void mutateAll(List<Car> localPopulation) {
        long start = System.nanoTime();
        localPopulation.parallelStream().filter(o -> RANDOM.nextDouble() < MUTATE_INDIVIDUAL_CHANGE).forEach(GeneticAlgorithm::mutate);
        totalMutateTime += (System.nanoTime() - start);
    }

    private static void mutate(Car car) {
        car.updateNumber();
        int m = 0;
        int no_m = 0;
        car.fitnessValue = 0;
        for (int i = 0; i < car.allThetas.size(); i++) {
            for (int j = 0; j < car.allThetas.get(i).data.length; j++) {
                if (RANDOM.nextDouble() < MUTATE_CHANGE) {
                    m++;
                    car.allThetas.get(i).data[j] += NeuralNetwork.getRandomWithinBoundaries();
                    car.allThetas.get(i).data[j] = NeuralNetwork.limitThetaValueToBoundaries(car.allThetas.get(i).data[j]);
                } else {
                    no_m++;
                }
            }
        }
    }
}
