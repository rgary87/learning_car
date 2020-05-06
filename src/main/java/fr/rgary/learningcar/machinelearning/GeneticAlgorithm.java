package fr.rgary.learningcar.machinelearning;

import com.sun.org.apache.bcel.internal.generic.POP;
import fr.rgary.learningcar.Car;
import fr.rgary.learningcar.Processor;
import fr.rgary.learningcar.base.Constant;
import fr.rgary.learningcar.base.PrintUtil;
import fr.rgary.learningcar.tracks.Track;
import fr.rgary.learningcar.trigonometry.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import static fr.rgary.learningcar.Processor.POPULATION;
import static fr.rgary.learningcar.Processor.toSelect;
import static fr.rgary.learningcar.base.Constant.MUTATE_CHANGE;
import static fr.rgary.learningcar.base.Constant.MUTATE_INDIVIDUAL_CHANGE;
import static fr.rgary.learningcar.base.Constant.RANDOM;

/**
 * Class GeneticAlgorithm.
 */
public class GeneticAlgorithm {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneticAlgorithm.class);
    //    public List<Car> POPULATION ; // = POPULATION;
//    public int population_size ; // = population_size;
//    public int selection_size; // = selection_size;
//    public int lucky_few_size; // = lucky_few_size;
//    public int mutation_chance; // = mutation_chance;
//    public int mutation_rate; // = mutation_rate;
//    public int to_breed; // = population_size - selection_size - lucky_few_size - 1 - to_regenerate;
//    public int to_regenerate; // = to_regenerate;
//    public List<Zone> polygons; // = polygon_zones;

    public static long totalBreedTime = 0;
    public static long totalMutateTime = 0;

    public static float prevBestFit = -1;

    public GeneticAlgorithm() {
//        this.polygons = Track.instance.zones;
    }

    public static void natureIsBeautiful() {
        POPULATION.parallelStream().forEach(Fitness::calcFitness);
        Collections.sort(POPULATION);
        PrintUtil.logPopulationNumberAndFitnessAsTable(POPULATION);
        if (POPULATION.get(0).fitnessValue < prevBestFit) {
            LOGGER.info("What ? ");
        }
        prevBestFit = POPULATION.get(0).fitnessValue;
        List<Car> localPopulation = new ArrayList<>();
        selection(localPopulation);
        mutateAll(localPopulation);
        localPopulation.add(0, POPULATION.get(0));
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
//            Car firstParent = selectBasedOnFitness(fitnessTotal);
//            Car secondParent = selectBasedOnFitness(fitnessTotal);
            Car firstParent = POPULATION.get(RANDOM.nextInt(POPULATION.size()));
            Car secondParent = POPULATION.get(RANDOM.nextInt(POPULATION.size()));

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
        car.fitnessValue = -1;
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
//        LOGGER.info("MUTATED {} TIMES OVER {}!", m, m + no_m);
    }
}
