package fr.rgary.learningcar;

import fr.rgary.learningcar.base.Constant;
import fr.rgary.learningcar.display.Draw;
import fr.rgary.learningcar.machinelearning.Fitness;
import fr.rgary.learningcar.machinelearning.NeuralNetwork;
import fr.rgary.learningcar.tracks.Track;
import fr.rgary.learningcar.trigonometry.Line;
import fr.rgary.learningcar.trigonometry.Point;
import fr.rgary.learningcar.trigonometry.Zone;
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
import static fr.rgary.learningcar.base.Constant.TRACK;

/**
 * Class Car.
 */
public class Car implements Comparable<Car> {
    public static final Logger LOGGER = LoggerFactory.getLogger(Car.class);

    public DMatrixRMaj theta1 = new DMatrixRMaj(FIRST_HIDDEN_LAYER_SIZE, INPUT_LAYER_SIZE + 1);
    public DMatrixRMaj theta2 = new DMatrixRMaj(SECOND_HIDDEN_LAYER_SIZE, FIRST_HIDDEN_LAYER_SIZE + 1);
    public DMatrixRMaj theta3 = new DMatrixRMaj(NUM_LABEL, SECOND_HIDDEN_LAYER_SIZE + 1);
    public List<DMatrixRMaj> allThetas = new ArrayList<>(Arrays.asList(theta1, theta2, theta3));

    public int sensorRange = 800;
    public List<Double> sensorDistances = new ArrayList<>(Arrays.asList(10000d, 10000d, 10000d, 10000d));
    public Point[] sensorIntersectPoints = new Point[]{null, null, null, null};

    public Point startPoint;
    public Point position;
    public double rotation = 0;

    public boolean active = true;
    public int moveStep = 5;
    public int defaultMaxMoveAllowed = 5000;
    public int moveDone = 0;
    public int maxZoneEntered = -1;

    public double rotationRate = Math.PI / 48;
    public double innerSensorRotation = Math.PI / 12;
    public double outerSensorRotation = Math.PI / 6;

    public float fitnessValue = -1;

    public int number;

    public static int maxNumber = 0;

    public synchronized int getMaxNumberAndIncrement() {
        Car.maxNumber += 1;
        return Car.maxNumber - 1;
    }

    public void updateNumber() {
        this.number = getMaxNumberAndIncrement();
    }

    public Car() {
        this.number = getMaxNumberAndIncrement();
        this.startPoint = Track.instance.startPoint;
        this.position = this.startPoint.clone();
        RandomMatrices_DDRM.addUniform(theta1, -1, 1, RANDOM);
        RandomMatrices_DDRM.addUniform(theta2, -1, 1, RANDOM);
        RandomMatrices_DDRM.addUniform(theta3, -1, 1, RANDOM);
    }

    public void reset() {
        this.position = TRACK.startPoint;
        this.rotation = 0;
        this.active = true;
        this.moveDone = 0;
        this.maxZoneEntered = -1;
    }

    public Car(Car o) {
        this.number = getMaxNumberAndIncrement();
        this.startPoint = Track.instance.startPoint;
        this.position = this.startPoint.clone();
        this.theta1 = new DMatrixRMaj(o.theta1);
        this.theta2 = new DMatrixRMaj(o.theta2);
        this.theta3 = new DMatrixRMaj(o.theta3);
    }

    public void moveMe() {
        if (active) {
            orderPerValues(NeuralNetwork.computeDirection(this.sensorDistances, this.theta1, this.theta2, this.theta3));
        }
    }

    //    ###################################################
    //    ####################### ORDERS ####################
    //    ###################################################
    public void orderPerValues(double[] direction) {
//        LOGGER.info("MOVE CAR");
        if (!this.active) {
            return;
        }
        this.moveDone += 1;
        if (this.moveDone >= this.defaultMaxMoveAllowed) {
            LOGGER.warn("TOO MANY MOVES ({})", this.moveDone);
            this.active = false;
            Fitness.calcFitness(this);
        }

        // TURN RIGHT
        this.rotation += (this.rotationRate * 2) * (Math.max(0, direction[Constant.CarOrder.TURN_RIGHT - 1]));

        // TURN LEFT
        this.rotation -= (this.rotationRate * 2) * (Math.max(0, direction[Constant.CarOrder.TURN_LEFT - 1]));

        // MOVE FORWARD
        this.position = Draw.matRotatePoint(this.position, this.position.clone().moveMe(Math.toIntExact(Math.round(this.moveStep * Math.max(0, direction[Constant.CarOrder.FORWARD - 1])))), this.rotation);
    }

    //    ###################################################
    //    ################# SENSORS UPDATE ##################
    //    ###################################################
    public List<Double> getSensorsValues() {
//        LOGGER.info("GET SENSORS FOR CAR");
        if (!this.active) {
            return this.sensorDistances;
        }

        this.sensorDistances = new ArrayList<>(Arrays.asList(10000d, 10000d, 10000d, 10000d));

        Line sensorFarLeft = new Line(this.position.clone(), new Point(this.position.X, this.position.Y + this.sensorRange));
        Line sensorLeft = new Line(this.position.clone(), new Point(this.position.X, this.position.Y + this.sensorRange));
        Line sensorFarRight = new Line(this.position.clone(), new Point(this.position.X, this.position.Y + this.sensorRange));
        Line sensorRight = new Line(this.position.clone(), new Point(this.position.X, this.position.Y + this.sensorRange));

        sensorFarLeft.E = Draw.matRotatePoint(this.position, sensorFarLeft.E, this.rotation - this.outerSensorRotation);
        sensorLeft.E = Draw.matRotatePoint(this.position, sensorLeft.E, this.rotation - this.innerSensorRotation);
        sensorFarRight.E = Draw.matRotatePoint(this.position, sensorFarRight.E, this.rotation + this.outerSensorRotation);
        sensorRight.E = Draw.matRotatePoint(this.position, sensorRight.E, this.rotation + this.innerSensorRotation);

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
                this.active = false;
                Fitness.calcFitness(this);
                break;
            }
//            LOGGER.info("DISTANCE IS {}", distance);
        }

        return this.sensorDistances;
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
//        return Math.round(((Car) o).fitnessValue - this.fitnessValue);
    }
}
