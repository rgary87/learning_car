package fr.rgary.learningcar.base;

import fr.rgary.learningcar.display.DrawLevelEnum;
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
        public static final int TURN_LEFT = 0;
        public static final int TURN_LEFT_SLOW_FORWARD = 3;
        public static final int FORWARD = 2;
        public static final int TURN_RIGHT_SLOW_FORWARD = 4;
        public static final int TURN_RIGHT = 1;
    }

    public static double MUTATE_INDIVIDUAL_CHANGE = 0.3;
    public static double MUTATE_CHANGE = 0.5;

    public static DrawLevelEnum DRAW_LEVEL = DrawLevelEnum.NO_DRAW;
    public static int VSYNC = 0;
    public static boolean PAUSE = false;
    public static boolean TRACK_MOUSE = false;
    public static boolean DRAW_THETA = false;

    public static int INTER_FRAME_DELAY = 0;

    public static Track TRACK;

    public static void setTRACK(Track TRACK) {
        Constant.TRACK = TRACK;
    }

}
