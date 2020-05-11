/*
 * Copyright (C) 2018 Eir.
 */
package fr.rgary.learningcar.display;

import fr.rgary.learningcar.Processor;
import fr.rgary.learningcar.base.Constant;
import fr.rgary.learningcar.tracks.JsonToTrack;
import fr.rgary.learningcar.trigonometry.Line;
import fr.rgary.learningcar.trigonometry.Point;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;

import static fr.rgary.learningcar.Processor.GENERATION;
import static fr.rgary.learningcar.base.Constant.INTER_FRAME_DELAY;
import static fr.rgary.learningcar.base.Constant.TRACK;
import static fr.rgary.learningcar.display.Draw.GLOBAL_HORIZONTAL_DISPLACEMENT;
import static fr.rgary.learningcar.display.Draw.GLOBAL_VERTICAL_DISPLACEMENT;
import static fr.rgary.learningcar.display.Draw.STATIC_ELEM;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
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


        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer


            if (!Constant.PAUSE) {
                processor.mainLogicalLoop();
            }
            Draw.drawTrack(Constant.TRACK);
            Draw.drawPopulation();

            frames++;
            if (System.currentTimeMillis() - previous > 1000) {
//                System.out.printf("%d FPS %n", frames);
                previous = System.currentTimeMillis();
                prevSecFPS = frames;
                frames = 0;
            }

            Draw.drawAnyText(String.format("%dFPS",prevSecFPS), 15, 45, STATIC_ELEM);
            Draw.drawAnyText(String.format("%d GEN",GENERATION), 15, 15, STATIC_ELEM);
            Draw.drawAnyText(String.format("%d ACTIVE",processor.activeCarCount), 15, 30, STATIC_ELEM);

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
        glfwSwapInterval(0);

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
                        Constant.updateDebugLevel();
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
                }
            }
            if (action == GLFW_REPEAT) {
            }
        });
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {

            Color drawingColor = new Color(255, 165, 0);
            double rot = 0;
            Point directionVector = null;

            if (action == GLFW_PRESS) {
                LOGGER.info("mouse pressed");
                Point mousePos = getMousePoint();
                if (drawingClass.start == null)
                    drawingClass.start = mousePos;
            }
            if (action == GLFW_RELEASE) {
                LOGGER.info("mouse release");
                drawingClass.end = getMousePoint();
                Draw.drawnLines.add(new Line(drawingClass.start, drawingClass.end, drawingColor));

                directionVector = getTwoPointDirectionVector(drawingClass.start, drawingClass.end);

                LOGGER.info("Adding line");
                Point leftBorderNewEnd;
                Point rightBorderNewEnd;
                int s = TRACK.borderTwo.size() - 1;
                int t = TRACK.borderOne.size() - 1;

                double abDist = Math.hypot((double) drawingClass.end.X - drawingClass.start.X, (double) drawingClass.end.Y - drawingClass.start.Y);

                double bcDist = 50;
                double acDist = Math.sqrt(Math.pow(abDist, 2) + Math.pow(bcDist, 2));
                leftBorderNewEnd = new Point(
                        (int) (drawingClass.start.X + (directionVector.X * (acDist / abDist))),
                        (int) (drawingClass.start.Y + (directionVector.Y * (acDist / abDist))));

                rightBorderNewEnd = new Point(
                        (int) (drawingClass.start.X + (directionVector.X * (acDist / abDist))),
                        (int) (drawingClass.start.Y + (directionVector.Y * (acDist / abDist))));

                rot = Math.cos(abDist / acDist);

                leftBorderNewEnd = Draw.matRotatePoint(drawingClass.start, leftBorderNewEnd, rot);
                rightBorderNewEnd = Draw.matRotatePoint(drawingClass.start, rightBorderNewEnd, -rot);
                Line newLeftBorderNewLine = new Line(TRACK.borderTwo.get(s).E, leftBorderNewEnd);
                Line newRightBorderNewLine = new Line(TRACK.borderOne.get(t).E, rightBorderNewEnd);
                TRACK.borderTwo.add(newLeftBorderNewLine);
                TRACK.borderOne.add(newRightBorderNewLine);
                TRACK.borders.add(newLeftBorderNewLine);
                TRACK.borders.add(newRightBorderNewLine);

                drawingClass.start = drawingClass.end;
            }
        });
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

    static class drawingClass {
        public static Point start = null;
        public static Point end = null;

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

}
