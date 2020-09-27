/*
 * Copyright (C) 2018 Eir.
 */
package fr.rgary.learningcar.display;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.rgary.learningcar.Car;
import fr.rgary.learningcar.Processor;
import fr.rgary.learningcar.base.Constant;
import fr.rgary.learningcar.dto.AllThetaDTO;
import fr.rgary.learningcar.dto.ThetaDTO;
import fr.rgary.learningcar.machinelearning.GeneticAlgorithm;
import fr.rgary.learningcar.tracks.JsonToTrack;
import fr.rgary.learningcar.trigonometry.Line;
import fr.rgary.learningcar.trigonometry.Point;
import org.ejml.data.DMatrixRMaj;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static fr.rgary.learningcar.Processor.POPULATION;
import static fr.rgary.learningcar.base.Constant.DRAW_THETA;
import static fr.rgary.learningcar.base.Constant.INTER_FRAME_DELAY;
import static fr.rgary.learningcar.base.Constant.TRACK;
import static fr.rgary.learningcar.base.Constant.TRACK_MOUSE;
import static fr.rgary.learningcar.base.Constant.VSYNC;
import static fr.rgary.learningcar.display.Draw.GLOBAL_HORIZONTAL_DISPLACEMENT;
import static fr.rgary.learningcar.display.Draw.GLOBAL_VERTICAL_DISPLACEMENT;
import static fr.rgary.learningcar.display.Draw.STATIC_ELEM;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorContentScale;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Class Display.
 */
public class Display {
    private static final Logger LOGGER = LoggerFactory.getLogger(Display.class);

    public static final int WIDTH = 800;
    public static final int HEIGHT = 1200;
    public static Font FONT;
    // The window handle
    private long window;

    public Display() {
    }

    public void run() {
        this.init();
        this.callBacks();
        this.loop();
    }


    private void loop() {

        // Set the clear color
        glClearColor(0.99f, 0.99f, 0.99f, 1.0f);
        glEnable(GL_COLOR_MATERIAL);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        Constant.setTRACK(JsonToTrack.buildTrack());

        long previous = System.currentTimeMillis();
        long frames = 0;
        long prevSecFPS = 0;
        Processor processor = new Processor();
        this.readBestCar();

        int infoLineHeight = 15;

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer


            if (!Constant.PAUSE) {
                processor.mainLogicalLoop();
            } else {
                Draw.drawAnyText("PAUSED", 15, infoLineHeight * 5, STATIC_ELEM);
            }
            Draw.drawZones();
            Draw.drawTrack(Constant.TRACK);
            Draw.drawPopulation();
            Draw.drawTheta();


            frames++;
            if (System.currentTimeMillis() - previous > 1000) {
//                System.out.printf("%d FPS %n", frames);
                previous = System.currentTimeMillis();
                prevSecFPS = frames;
                frames = 0;
            }


            Draw.drawInfoTexts(prevSecFPS, processor, infoLineHeight);

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            try {
                Thread.sleep(INTER_FRAME_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        saveBestCar();
    }



    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        long monitor = glfwGetPrimaryMonitor();

        try (MemoryStack s = stackPush()) {
            FloatBuffer px = s.mallocFloat(1);
            FloatBuffer py = s.mallocFloat(1);

            glfwGetMonitorContentScale(monitor, px, py);
        }

        // Create the window
        this.window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if (this.window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetWindowSizeCallback(window, this::windowSizeChanged);

        GLFWVidMode vidmode = Objects.requireNonNull(glfwGetVideoMode(monitor));

        glfwSetWindowPos(
                window,
                (vidmode.width() - WIDTH) / 2 - vidmode.width(),
                (vidmode.height() - HEIGHT) / 2
        );


        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        // Enable v-sync
        glfwSwapInterval(VSYNC);

        // Make the window visible
        glfwShowWindow(window);

    }

    private void callBacks() {
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (action == GLFW_RELEASE) {
                switch (key) {
                    case GLFW_KEY_ESCAPE:
                    case GLFW_KEY_Q:
                        glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
                        break;
                    case GLFW_KEY_D:
                        Constant.DRAW_LEVEL = Constant.DRAW_LEVEL.getNext();
                        break;
                    case GLFW_KEY_P:
                        Constant.PAUSE = !Constant.PAUSE;
                        break;
                    case GLFW_KEY_UP:
                        Draw.GLOBAL_VERTICAL_DISPLACEMENT += 10;
                        break;
                    case GLFW_KEY_DOWN:
                        Draw.GLOBAL_VERTICAL_DISPLACEMENT -= 10;
                        break;
                    case GLFW_KEY_LEFT:
                        Draw.GLOBAL_HORIZONTAL_DISPLACEMENT -= 10;
                        break;
                    case GLFW_KEY_RIGHT:
                        Draw.GLOBAL_HORIZONTAL_DISPLACEMENT += 10;
                        break;
                    case GLFW_KEY_M:
                        TRACK_MOUSE = !TRACK_MOUSE;
                        break;
                    case GLFW_KEY_O:
                        TRACK.printSupplementalLines();
                        break;
                    case GLFW_KEY_T:
                        DRAW_THETA = !DRAW_THETA;
                        break;
                    case GLFW_KEY_V:
                        if (VSYNC == 0) {
                            VSYNC = 1;
                        } else {
                            VSYNC = 0;
                        }
                        glfwSwapInterval(VSYNC);
                }
            }
            if (action == GLFW_REPEAT) {
            }
        });
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (TRACK_MOUSE && action == GLFW_PRESS) {
                this.saveMouseStart();
            }
            if (TRACK_MOUSE && action == GLFW_RELEASE) {
                this.saveMouseEnd();
            }
        });
    }

    public static Point MOUSE_START = null;
    public static Point MOUSE_END = null;

    private void saveMouseEnd() {
        Color drawingColor = new Color(255, 165, 0);
        double rot = 0;
        Point directionVector = null;

        LOGGER.info("mouse release");
        MOUSE_END = getMousePoint();
        Draw.drawnLines.add(new Line(MOUSE_START, MOUSE_END, drawingColor));
        LOGGER.info("Start: {} | End: {}", MOUSE_START, MOUSE_END);
        Draw.drawCrossMark(MOUSE_START);
        Draw.drawCrossMark(MOUSE_END);

        directionVector = getTwoPointDirectionVector(MOUSE_START, MOUSE_END);

        LOGGER.info("Adding line");
        Point leftBorderNewEnd;
        Point rightBorderNewEnd;
        int s = TRACK.borderTwo.size() - 1;
        int t = TRACK.borderOne.size() - 1;

        double abDist = Math.hypot((double) MOUSE_END.X - MOUSE_START.X, (double) MOUSE_END.Y - MOUSE_START.Y);
        double bcDist = 50;
        double acDist = Math.sqrt(Math.pow(abDist, 2) + Math.pow(bcDist, 2));
        leftBorderNewEnd = new Point(
                (int) (MOUSE_START.X + (directionVector.X * (acDist / abDist))),
                (int) (MOUSE_START.Y + (directionVector.Y * (acDist / abDist))));

        rightBorderNewEnd = new Point(
                (int) (MOUSE_START.X + (directionVector.X * (acDist / abDist))),
                (int) (MOUSE_START.Y + (directionVector.Y * (acDist / abDist))));

        rot = Math.sin(bcDist / acDist);

        leftBorderNewEnd = Draw.matRotatePoint(MOUSE_START, leftBorderNewEnd, rot);
        rightBorderNewEnd = Draw.matRotatePoint(MOUSE_START, rightBorderNewEnd, -rot);
        Line newLeftBorderNewLine = new Line(TRACK.borderTwo.get(s).E, leftBorderNewEnd);
        Line newRightBorderNewLine = new Line(TRACK.borderOne.get(t).E, rightBorderNewEnd);
        TRACK.addToBorderTwo(newLeftBorderNewLine);
        TRACK.addToBorderOne(newRightBorderNewLine);
        MOUSE_START = MOUSE_END;
    }

    private void saveMouseStart() {
        LOGGER.info("mouse pressed");
        Point mousePos = getMousePoint();
        if (MOUSE_START == null)
            MOUSE_START = mousePos;
    }

    private Point getMousePoint() {
        DoubleBuffer b1 = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer b2 = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, b1, b2);
        int x = (int) b1.get(0) - GLOBAL_HORIZONTAL_DISPLACEMENT;
        int y = (int) b2.get(0) - GLOBAL_VERTICAL_DISPLACEMENT;
        return new Point(x, y);
    }

    private void windowSizeChanged(long window, int width, int height) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, WIDTH, HEIGHT, 0.0, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);
    }

    public Point getTwoPointDirectionVector(Point a, Point b) {
        return new Point(b.X - a.X, b.Y - a.Y);
    }

//    public double getRotationFromPoints(Point b, Point c) {
//
//        LOGGER.info("B: {}, {}", b.X, b.Y);
//        LOGGER.info("C: {}, {}", c.X, c.Y);
//
//        // Vertical line
//        if (b.X == c.X) {
//            if (b.Y > c.Y) {
//                LOGGER.info("Going up");
//                return 1 * Math.PI;
//            } else {
//                LOGGER.info("Going down");
//                return 0 * Math.PI;
//            }
//        }
//        // Horizontal line
//        if (b.Y == c.Y) {
//            if (b.X > c.X) {
//                LOGGER.info("Going left");
//                return 1.5 * Math.PI;
//            } else {
//                LOGGER.info("Going right");
//                return 0.5 * Math.PI;
//            }
//        }
//
//        Point a;
//        double angleRadianB;
//        // Going up and right (1)
//        if (c.X > b.X && c.Y < b.Y) {
//            LOGGER.info("Going up and right");
//            a = new Point(c.X, b.Y);
//            angleRadianB = Math.atan((double) (a.Y - c.Y) / (double) (a.X - b.X));
////            angleRadianB += 0.5 * Math.PI;
//            LOGGER.info("Degrees : {}", angleRadianB * (180/Math.PI));
//        }
//        else
//        //Going up and left (3)
//        if (c.X < b.X && c.Y < b.Y) {
//            LOGGER.info("Going up and left");
//            a = new Point(c.X, b.Y);
//            angleRadianB = Math.atan((double) (a.Y - c.Y) / (double) (b.X - a.X));
////            angleRadianB += 1 * Math.PI;
//            LOGGER.info("Degrees : {}", angleRadianB * (180/Math.PI));
//        }
//        else
//        //Going down and right (7)
//        if (c.X > b.X && c.Y > b.Y) {
//            LOGGER.info("Going down and right");
//            a = new Point(b.X, c.Y);
//            angleRadianB = Math.atan((double) (c.X - a.X) / (double) (a.Y - b.Y));
////            angleRadianB += 0 * Math.PI;
//            LOGGER.info("Degrees : {}", angleRadianB * (180/Math.PI));
//        }
//        else
//        //Going down and left (5)
//        {
//            LOGGER.info("Going down and left");
//            a = new Point(b.X, c.Y);
//            angleRadianB = Math.atan((double) (a.X - c.X) / (double) (a.Y - b.Y));
////            angleRadianB += 1.5 * Math.PI;
//            LOGGER.info("Degrees : {}", angleRadianB * (180/Math.PI));
//        }
//        return angleRadianB;
//    }
    public static void saveBestCar() {
        saveBestCar("./src/main/resources/best_car.json", GeneticAlgorithm.best);
    }

    public static void saveBestCar(String fileName, Car best) {
        if (best == null) return;
        Collections.sort(POPULATION);
        List<ThetaDTO> thetas = new ArrayList<>();
        for (int c = 0; c < 5; c++) {
            if (c != 0) best = POPULATION.get(c);
            for (int i = 0; i < best.allThetas.size(); i++) {
                thetas.add(new ThetaDTO(best.allThetas.get(i).data, best.allThetas.get(i).numRows, best.allThetas.get(i).numCols));
            }
        }
        try {
            File file = new File(fileName);
            file.createNewFile();
            new ObjectMapper().writeValue(file, new AllThetaDTO(thetas));
        } catch (IOException ignore ) { }
    }

    private void readBestCar() {
        try {
            File file = new File("./src/main/resources/best_car.json");
            AllThetaDTO bestValues = new ObjectMapper().readValue(file, AllThetaDTO.class);

            for (int savedCars = 0; savedCars < 5; savedCars++) {
                    POPULATION.get(savedCars).theta1 = new DMatrixRMaj(
                            bestValues.thetas.get(savedCars * 3 + 0).numRows,
                            bestValues.thetas.get(savedCars * 3 + 0).numCols, true,
                            bestValues.thetas.get(savedCars * 3 + 0).data);
                    POPULATION.get(savedCars).theta2 = new DMatrixRMaj(
                            bestValues.thetas.get(savedCars * 3 + 1).numRows,
                            bestValues.thetas.get(savedCars * 3 + 1).numCols, true,
                            bestValues.thetas.get(savedCars * 3 + 1).data);
                    POPULATION.get(savedCars).theta3 = new DMatrixRMaj(
                            bestValues.thetas.get(savedCars * 3 + 2).numRows,
                            bestValues.thetas.get(savedCars * 3 + 2).numCols, true,
                            bestValues.thetas.get(savedCars * 3 + 2).data);
                    POPULATION.get(savedCars).allThetas = new ArrayList<>(Arrays.asList(POPULATION.get(savedCars).theta1, POPULATION.get(savedCars).theta2, POPULATION.get(savedCars).theta3));
            }
        } catch (IOException e) { }
    }

}
