package fr.rgary.learningcar;

import fr.rgary.learningcar.base.Constant;
import fr.rgary.learningcar.display.Draw;
import fr.rgary.learningcar.machinelearning.Fitness;
import fr.rgary.learningcar.machinelearning.NeuralNetwork;
import fr.rgary.learningcar.tracks.Track;
import fr.rgary.learningcar.trigonometry.Line;
import fr.rgary.learningcar.trigonometry.Point;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.RandomMatrices_DDRM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static fr.rgary.learningcar.base.Constant.FIRST_HIDDEN_LAYER_SIZE;
import static fr.rgary.learningcar.base.Constant.INPUT_LAYER_SIZE;
import static fr.rgary.learningcar.base.Constant.NUM_LABEL;
import static fr.rgary.learningcar.base.Constant.RANDOM;
import static fr.rgary.learningcar.base.Constant.SECOND_HIDDEN_LAYER_SIZE;

/**
 * Class Car.
 */
public class Car implements Runnable, Comparable<Car> {
    public static final Logger LOGGER = LoggerFactory.getLogger(Car.class);
    public static int maxNumber = 0;
    public DMatrixRMaj theta1 = new DMatrixRMaj(FIRST_HIDDEN_LAYER_SIZE, INPUT_LAYER_SIZE + 1);
    public DMatrixRMaj theta2 = new DMatrixRMaj(SECOND_HIDDEN_LAYER_SIZE, FIRST_HIDDEN_LAYER_SIZE + 1);
    public DMatrixRMaj theta3 = new DMatrixRMaj(NUM_LABEL, SECOND_HIDDEN_LAYER_SIZE + 1);
    public List<DMatrixRMaj> allThetas;
    public int sensorRange = 800;
    public List<Double> sensorDistances = new ArrayList<>(Arrays.asList(10000d, 10000d, 10000d, 10000d));
    public Point[] sensorIntersectPoints = new Point[]{null, null, null, null};
    public Point startPoint;
    public Point position;
    public double rotation = 0;
    public boolean active = true;
    public int moveStep = 5;
    public int defaultMaxMoveAllowed = 50000;
    public int moveDone = 0;
    public int maxZoneEntered = -1;
    public double rotationRate = Math.PI / 48;
    public double innerSensorRotation = Math.PI / 12;
    public double outerSensorRotation = Math.PI / 6;
    public float fitnessValue = -1;
    public int number;
    public int laps = 0;
    public int drawnInactive = 0;
    public boolean selected = false;

    public Car() {
        this.startPoint = Track.instance.startPoint;
        this.position = this.startPoint.clone();
        RandomMatrices_DDRM.addUniform(this.theta1, -1, 1, RANDOM);
        RandomMatrices_DDRM.addUniform(this.theta2, -1, 1, RANDOM);
        RandomMatrices_DDRM.addUniform(this.theta3, -1, 1, RANDOM);
        this.allThetas = new ArrayList<>(Arrays.asList(this.theta1, this.theta2, this.theta3));
    }

    public Car(int idx) {
        this();
        this.number = idx;
    }

//    public Car(Car o) {
//        this.startPoint = Track.instance.startPoint;
//        this.position = this.startPoint.clone();
//        this.theta1 = new DMatrixRMaj(o.theta1);
//        this.theta2 = new DMatrixRMaj(o.theta2);
//        this.theta3 = new DMatrixRMaj(o.theta3);
//        this.allThetas = new ArrayList<>(Arrays.asList(this.theta1, this.theta2, this.theta3));
//    }

    public void reset() {
        this.position = this.startPoint.clone();
        this.rotation = 0;
        this.active = true;
        this.moveDone = 0;
        this.maxZoneEntered = -1;
        this.drawnInactive = 0;
        this.laps = 0;
        this.selected = false;
        this.fitnessValue = -1;
    }

    public void moveMe() {
        orderPerValues(NeuralNetwork.computeDirection(this.sensorDistances, this.theta1, this.theta2, this.theta3));
    }

    //    ###################################################
    //    ####################### ORDERS ####################
    //    ###################################################
    public void orderPerValues(double[] direction) {
//        LOGGER.info("MOVE CAR");
        if (!this.isActive()) {
            return;
        }
        this.moveDone += 1;
        if (this.moveDone >= this.defaultMaxMoveAllowed) {
            LOGGER.warn("TOO MANY MOVES ({})", this.moveDone);
            this.deactivate();
        }

        // TURN RIGHT
        this.rotation += (this.rotationRate * 2) * (Math.max(0, direction[Constant.CarOrder.TURN_RIGHT]));

        // TURN LEFT
        this.rotation -= (this.rotationRate * 2) * (Math.max(0, direction[Constant.CarOrder.TURN_LEFT]));

        // MOVE FORWARD
        this.position = Draw.matRotatePointForCar(this.position, this.position.clone().moveMe(Math.toIntExact(Math.round(this.moveStep * Math.max(0, direction[Constant.CarOrder.FORWARD])))), this.rotation);
    }

    //    ###################################################
    //    ################# SENSORS UPDATE ##################
    //    ###################################################
    public void getSensorsValues() {
//        LOGGER.info("GET SENSORS FOR CAR");
        this.sensorDistances = new ArrayList<>(Arrays.asList(10000d, 10000d, 10000d, 10000d));

        Line sensorFarLeft = new Line(this.position.clone(), new Point(this.position.X, this.position.Y + this.sensorRange));
        Line sensorLeft = new Line(this.position.clone(), new Point(this.position.X, this.position.Y + this.sensorRange));
        Line sensorRight = new Line(this.position.clone(), new Point(this.position.X, this.position.Y + this.sensorRange));
        Line sensorFarRight = new Line(this.position.clone(), new Point(this.position.X, this.position.Y + this.sensorRange));

        sensorFarLeft.E = Draw.matRotatePoint(this.position, sensorFarLeft.E, this.rotation - this.outerSensorRotation);
        sensorLeft.E = Draw.matRotatePoint(this.position, sensorLeft.E, this.rotation - this.innerSensorRotation);
        sensorRight.E = Draw.matRotatePoint(this.position, sensorRight.E, this.rotation + this.innerSensorRotation);
        sensorFarRight.E = Draw.matRotatePoint(this.position, sensorFarRight.E, this.rotation + this.outerSensorRotation);

        List<Line> sensorsLines = new ArrayList<>(Arrays.asList(
                sensorFarLeft,
                sensorLeft,
                sensorFarRight,
                sensorRight
        ));

        int sensorIdx = 0;
        for (Line sensorLine : sensorsLines) {
            final List<Point> intersections = new ArrayList<>(10);
            for (Line borderLine : Track.instance.borders) {
                Point intersection = Line.calculateIntersectionPoint(sensorLine, borderLine);
                if (intersection != null) {
                    intersections.add(intersection);
                }
            }

            for (Point intersection : intersections) {
                Double distance = Math.hypot(intersection.X - this.position.X, intersection.Y - this.position.Y);
                if (this.sensorDistances.get(sensorIdx) > distance) {
                    this.sensorDistances.set(sensorIdx, distance);
                    this.sensorIntersectPoints[sensorIdx] = intersection.clone();
                }
            }

            sensorIdx++;
        }

        for (Double distance : sensorDistances) {
            if (distance > 1000 && Constant.TRACK.getZoneNumberPerPosition(this.position) != 0) {
//                LOGGER.warn("WEIRD DISTANCE S: {}", distance);
            }
            if (distance < 30) {
                this.deactivate();
                break;
            }
//            LOGGER.info("DISTANCE IS {}", distance);
        }
        Fitness.calcFitness(this);
        if (this.moveDone > 500 && this.fitnessValue < this.moveDone) {
//            LOGGER.info("Okay I'm reaaaally bad, let's die. (fitness: {}, move: {}", this.fitnessValue, this.moveDone);
            this.active = false;
            this.fitnessValue = 0;
        }

        if (this.moveDone > 500) {
            this.deactivate();
        }
    }

    public void deactivate() {
        this.active = false;
        Fitness.calcFitness(this);

    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return number == car.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return "Car{" +
                "number=" + number +
                ", fitnessValue=" + fitnessValue +
                ", moveDone=" + moveDone +
                ", maxZoneEntered=" + maxZoneEntered +
                '}';
    }

    @Override
    public int compareTo(Car o) {
        if (o.fitnessValue == this.fitnessValue)
            return 0;
        return o.fitnessValue > this.fitnessValue ? 1 : -1;
    }

    public float getDifference(Car o) {
        float accumulator = 0;
        for (int i = 0; i < this.allThetas.size(); i++) {
            for (int j = 0; j < this.allThetas.get(i).data.length; j++) {
                accumulator += Math.abs(this.allThetas.get(i).data[j] - o.allThetas.get(i).data[j]);
            }
        }
        return accumulator;
    }

    public void resetIfNotSelected() {
        if (!selected) {
            this.reset();
        }
    }

    @Override
    public void run() {
        if (active) {
            this.getSensorsValues();
            this.moveMe();
        } else if (fitnessValue == -1) {
            Fitness.calcFitness(this);
        }
    }
}
