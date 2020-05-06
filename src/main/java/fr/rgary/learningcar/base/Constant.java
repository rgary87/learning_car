package fr.rgary.learningcar.base;

import fr.rgary.learningcar.tracks.Track;

import java.util.Random;

/**
 * Class Constant.
 */
public class Constant {
    public static final int INPUT_LAYER_SIZE = 4;
    public static final int FIRST_HIDDEN_LAYER_SIZE = 5;
    public static final int SECOND_HIDDEN_LAYER_SIZE = 4;
    public static final int NUM_LABEL = 3;

    public static final int CAR_LENGTH = 50;
    public static final int CAR_WIDTH = 25;

    public static final Random RANDOM = new Random();

    public class CarOrder {
        public static final int TURN_LEFT = 1;
        public static final int TURN_LEFT_SLOW_FORWARD = 4;
        public static final int FORWARD = 3;
        public static final int TURN_RIGHT_SLOW_FORWARD = 5;
        public static final int TURN_RIGHT = 2;
    }

    private static final int MAX_DEBUG_LEVEL = 5;
    private static final int MIN_DEBUG_LEVEL = 0;

    public static double MUTATE_INDIVIDUAL_CHANGE = 0.3;
    public static double MUTATE_CHANGE = 0.5;

    public static int DEBUG_LEVEL = 3;
    public static boolean PAUSE = true;

    public static int INTER_FRAME_DELAY = 0;

    public static Track TRACK;

    public static void setTRACK(Track TRACK) {
        Constant.TRACK = TRACK;
    }

    public static void updateDebugLevel() {
        DEBUG_LEVEL++;
        if (DEBUG_LEVEL > MAX_DEBUG_LEVEL) {
            DEBUG_LEVEL = MIN_DEBUG_LEVEL;
        }
    }
}
