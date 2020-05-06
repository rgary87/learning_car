package fr.rgary.learningcar.machinelearning;

import org.apache.commons.lang3.ArrayUtils;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

/**
 * Class NeuralNetwork.
 */
public class NeuralNetwork {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeuralNetwork.class);

    public static final Random RAND = new Random();

    public static Double sigmoid(Double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public static DMatrixRMaj sigmoid(DMatrixRMaj m) {
        for (int i = 0; i < m.data.length; i++) {
            m.data[i] = sigmoid(m.data[i]);
        }
        return m;
    }

    public static DMatrixRMaj addIdentity(DMatrixRMaj matrix) {
        double[] identity = new double[]{1};
        double[] newData = ArrayUtils.addAll(identity, matrix.data);
        matrix.reshape(matrix.numRows, matrix.numCols + 1);
        matrix.setData(newData);
        return matrix;
    }

    public static DMatrixRMaj transpose(DMatrixRMaj matrix) {
        DMatrixRMaj res = null;
        res = CommonOps_DDRM.transpose(matrix, res);
        return res;
    }

    public static DMatrixRMaj dot(DMatrixRMaj a, DMatrixRMaj b) {
        DMatrixRMaj c = new DMatrixRMaj(a);
        CommonOps_DDRM.mult(a, b, c);
        return c;
    }

    public static double[] computeDirection(List<Double> sensorsDistance, DMatrixRMaj theta1, DMatrixRMaj theta2, DMatrixRMaj theta3) {
        DMatrixRMaj X = new DMatrixRMaj(1, 4);
        X.setData(sensorsDistance.stream().mapToDouble(d -> d).toArray());
        DMatrixRMaj h1 = sigmoid(dot(addIdentity(X), transpose(theta1)));
        DMatrixRMaj h2 = sigmoid(dot(addIdentity(h1), transpose(theta2)));
        DMatrixRMaj h3 = sigmoid(dot(addIdentity(h2), transpose(theta3)));
        return h3.data;
    }

    public static double limitThetaValueToBoundaries(double d) {
        if (-1 < d && d < 1) {
            return d;
        }
        if (d < 0) {
            return d + 1;
        }
        return d - 1;
    }

    public static double getRandomWithinBoundaries() {
        return (2 * RAND.nextDouble()) - 1;
    }
}
