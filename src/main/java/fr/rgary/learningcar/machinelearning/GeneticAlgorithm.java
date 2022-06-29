package fr.rgary.learningcar.machinelearning;

import fr.rgary.learningcar.Car;
import fr.rgary.learningcar.Population;
import fr.rgary.learningcar.Processor;
import fr.rgary.learningcar.base.PrintUtil;
import org.ejml.data.DMatrixRMaj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import static fr.rgary.learningcar.Population.CARLIST;
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

    public static float maximumDifference = 0;
    public static boolean initialized = false;

    public static Car best;

    private GeneticAlgorithm() {
//        this.polygons = Track.instance.zones;
    }

    public static void natureIsBeautiful() {
        if (!initialized) GeneticAlgorithm.init();

        Collections.sort(CARLIST);
        PrintUtil.logPopulationNumberAndFitnessAsTable(CARLIST);
        best = Processor.POPULATION.getBest();
        best.selected = true;
        CARLIST.parallelStream().forEach(car -> Fitness.adjustFitnessOnDifference(car, best, maximumDifference));
        Collections.sort(CARLIST);
        if (best.fitnessValue < prevBestFit) {
            LOGGER.info("What ? ");
        }
        prevBestFit = best.fitnessValue;
//        List<Car> localPopulation = new ArrayList<>();
        selection();//localPopulation);
        mutateAll();//localPopulation);
//        localPopulation.add(0, best);
//        CARLIST = new ArrayList<>(localPopulation);
        CARLIST.forEach(Car::reset);
    }

    private static void selectBasedOnFitness(float fitnessTotal) {
        double rand = RANDOM.nextDouble() * fitnessTotal;
        double runningSum = 0;
        Car prev = null;
        for (Car car : CARLIST) {
            runningSum += car.fitnessValue;
            if (prev != null) {
                car.getDifference(prev);
            }
            prev = car;
            if (runningSum > rand) {
                if (car.selected) {
                    continue;
                }
                car.selected = true;
                Population.selected += 1;
                return;
            }
        }
    }

    private static void breedChild(Car c1, Car c2, Car toBreed) {
        long start = System.nanoTime();
        for (int i = 0; i < c1.allThetas.size(); i++) {
            for (int j = 0; j < c1.allThetas.get(i).data.length; j++) {
                toBreed.allThetas.get(i).data[j] = RANDOM.nextDouble() < 0.5 ? c1.allThetas.get(i).data[j] : c2.allThetas.get(i).data[j];
            }
        }
        totalBreedTime += (System.nanoTime() - start);
        toBreed.selected = true;
        Population.selected += 1;
    }

    private static void selection(/*List<Car> localPopulation*/) {
        float fitnessTotal = Fitness.getTotalFitness();
        while (Population.selected < Processor.toSelect) {
            LOGGER.warn("Do I REALLY come in here ?");
//            localPopulation.add(new Car(selectBasedOnFitness(fitnessTotal)));
            selectBasedOnFitness(fitnessTotal);
        }

        CARLIST.parallelStream().forEach(Car::resetIfNotSelected);
        Collections.sort(CARLIST);

        while (true) {
            Car availableCar = Population.getAvailableCar();
            if (availableCar == null) break;
            int i = RANDOM.nextInt(Processor.toSelect);
            int j = i;
            while (j == i) {
                j = RANDOM.nextInt(Processor.toSelect);
            }
            Car firstParent = CARLIST.get(i);
            Car secondParent = CARLIST.get(j);

            breedChild(firstParent, secondParent, availableCar);
        }
    }

    private static void mutateAll() {
        long start = System.nanoTime();
        CARLIST.parallelStream().forEach(GeneticAlgorithm::mutate);
        totalMutateTime += (System.nanoTime() - start);
    }

    private static void mutate(Car car) {
        if (RANDOM.nextDouble() > MUTATE_INDIVIDUAL_CHANGE || !car.selected || car.number == Population.getBest().number) return;
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

    private static void init() {
        List<DMatrixRMaj> allThetas = CARLIST.get(0).allThetas;
        int thetaCount = 0;
        for (DMatrixRMaj matrixRMaj : allThetas) {
            thetaCount += matrixRMaj.data.length;
        }
        maximumDifference = thetaCount * 2;
        initialized = true;
    }
}
