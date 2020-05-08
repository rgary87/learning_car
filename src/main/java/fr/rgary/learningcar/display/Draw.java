package fr.rgary.learningcar.display;

import fr.rgary.learningcar.Car;
import fr.rgary.learningcar.Processor;
import fr.rgary.learningcar.tracks.Track;
import fr.rgary.learningcar.trigonometry.Line;
import fr.rgary.learningcar.trigonometry.Point;
import org.ejml.simple.SimpleMatrix;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static fr.rgary.learningcar.base.Constant.CAR_LENGTH;
import static fr.rgary.learningcar.base.Constant.CAR_WIDTH;
import static fr.rgary.learningcar.base.Constant.DEBUG_LEVEL;
import static fr.rgary.learningcar.base.Constant.FIRST_HIDDEN_LAYER_SIZE;
import static fr.rgary.learningcar.base.Constant.INPUT_LAYER_SIZE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.stb.STBEasyFont.stb_easy_font_print;

/**
 * Class Draw.
 */
public class Draw {

    public static int GLOBAL_HORIZONTAL_DISPLACEMENT = 0;
    public static int GLOBAL_VERTICAL_DISPLACEMENT = 0;
    public static int STATIC_ELEM = 0;
    public static int MOVING_ELEM = 1;
    static Color activeCarColor = new Color(1, 1, 1);
    static Color inactiveCarColor = new Color(1, 0, 0, 1);
    public static List<Line> drawnLines = new ArrayList<>();

    public static void drawAnyText(String text, float x, float y, int movingPart) {
        Draw.drawAnyText(text, x, y, 169f, 183f, 198f, 1f, 1f, movingPart);
    }

    public static void drawAnyText(String text, float x, float y, Color color, int movingPart) {
        Draw.drawAnyText(text, x, y, (float) color.R * 255, (float) color.G * 255, (float) color.B * 255, 1f, 1f, movingPart);
    }

    public static void drawAnyText(String text, float x, float y, Color color, float scaleX, float scaleY, int movingPart) {
        Draw.drawAnyText(text, x, y, (float) color.R * 255, (float) color.G * 255, (float) color.B * 255, scaleX, scaleY, movingPart);
    }

    public static void drawTrack(Track track) {
        track.borders.forEach(Draw::drawLine);
        if (DEBUG_LEVEL > 3) {
            track.fitnessZoneLines.forEach(Draw::drawLine);
        }
        Draw.drawnLines.forEach(Draw::drawLine);
    }

    public static void drawPopulation() {
        for (Car car : Processor.POPULATION) {
            drawCar(car);
        }
        drawThetaTable();
    }

    private static void drawCar(Car car) {
        if (DEBUG_LEVEL > 2) {
            drawSensors(car);
        }

        Point positionVector = car.position.clone();
        double rotation = car.rotation;
        boolean active = car.active;
        int carLength = CAR_LENGTH;
        int carWidth = CAR_WIDTH;

        Point cornerTopLeft = new Point(positionVector.X - (carWidth / 2), positionVector.Y - (carLength / 2));
        Point cornerTopRight = new Point(positionVector.X + (carWidth / 2), positionVector.Y - (carLength / 2));
        Point cornerBottomRight = new Point(positionVector.X + (carWidth / 2), positionVector.Y + (carLength / 2));
        Point cornerBottomLeft = new Point(positionVector.X - (carWidth / 2), positionVector.Y + (carLength / 2));

        cornerTopLeft = matRotatePoint(positionVector, cornerTopLeft, rotation);
        cornerTopRight = matRotatePoint(positionVector, cornerTopRight, rotation);
        cornerBottomRight = matRotatePoint(positionVector, cornerBottomRight, rotation);
        cornerBottomLeft = matRotatePoint(positionVector, cornerBottomLeft, rotation);

        if (active) {
            drawQuad(cornerTopLeft, cornerTopRight, cornerBottomRight, cornerBottomLeft, activeCarColor);
        } else {
            drawLine(new Line(
                    new Point(cornerTopLeft.X, cornerTopLeft.Y),
                    new Point(cornerTopRight.X, cornerTopRight.Y),
                    inactiveCarColor));
            drawLine(new Line(
                    new Point(cornerTopRight.X, cornerTopRight.Y),
                    new Point(cornerBottomRight.X, cornerBottomRight.Y),
                    inactiveCarColor));
            drawLine(new Line(
                    new Point(cornerBottomRight.X, cornerBottomRight.Y),
                    new Point(cornerBottomLeft.X, cornerBottomLeft.Y),
                    inactiveCarColor));
            drawLine(new Line(
                    new Point(cornerBottomLeft.X, cornerBottomLeft.Y),
                    new Point(cornerTopLeft.X, cornerTopLeft.Y),
                    inactiveCarColor));
        }
    }

    private static void drawSensors(Car car) {
        glBegin(GL_LINES);
        if (car.active) {
            glColor3d(0 / 255d, 0 / 255d, 255 / 255d);
        } else {
            glColor3d(110 / 255d, 255 / 255d, 255 / 255d);
        }
        for (int i = 0; i < car.sensorIntersectPoints.length; i++) {
            if (car.sensorIntersectPoints[i] == null) {
                continue;
            }
            glVertex2d(car.position.X + GLOBAL_HORIZONTAL_DISPLACEMENT, car.position.Y + GLOBAL_VERTICAL_DISPLACEMENT);
            glVertex2d(car.sensorIntersectPoints[i].X + GLOBAL_HORIZONTAL_DISPLACEMENT, car.sensorIntersectPoints[i].Y + GLOBAL_VERTICAL_DISPLACEMENT);
        }
        glEnd();
    }

    private static void drawThetaTable() {
        Color tableColor = new Color(208, 240, 192);
        Color overZeroTextColor = new Color(57, 68, 188);
        Color underZeroTextColor = new Color(246, 70, 91);
        drawLine(new Line(new Point(0, 800), new Point(800, 800), tableColor), STATIC_ELEM);

        int cellWidth = 760 / (INPUT_LAYER_SIZE + 1);
        int cellHeight = 360 / (FIRST_HIDDEN_LAYER_SIZE);

        for (int i = 0; i < INPUT_LAYER_SIZE + 1; i++) {
            for (int j = 0; j < FIRST_HIDDEN_LAYER_SIZE; j++) {
                int posXStart = 20 + (i * cellWidth);
                int posYStart = 820 + (j * cellHeight);
                drawThetaSquare(new Point(posXStart, posYStart), cellWidth, cellHeight, tableColor);
                if (Processor.POPULATION.get(0).theta1.data[(i * INPUT_LAYER_SIZE) + j] > 0) {
                    drawAnyText(String.format("%1.3f", Processor.POPULATION.get(0).theta1.data[(i * INPUT_LAYER_SIZE) + j]),
                            posXStart + cellWidth / 4 + 15, posYStart + cellHeight / 3 + 5,
                            overZeroTextColor,
                            2f, 2f, STATIC_ELEM);
                } else {
                    drawAnyText(String.format("%1.3f", Processor.POPULATION.get(0).theta1.data[(i * INPUT_LAYER_SIZE) + j]),
                            posXStart + cellWidth / 4 + 5, posYStart + cellHeight / 3 + 5,
                            underZeroTextColor,
                            2f, 2f, STATIC_ELEM);
                }
            }
        }

    }

    public static void drawLine(Line line) {
        drawLine(line, 1);
    }

    private static void drawLine(Line line, int movingPart) {
        glBegin(GL_LINES);
        glColor3d(line.C.R, line.C.G, line.C.B);
        glVertex2d(line.S.X + (GLOBAL_HORIZONTAL_DISPLACEMENT * movingPart), line.S.Y + (GLOBAL_VERTICAL_DISPLACEMENT * movingPart));
        glVertex2d(line.E.X + (GLOBAL_HORIZONTAL_DISPLACEMENT * movingPart), line.E.Y + (GLOBAL_VERTICAL_DISPLACEMENT * movingPart));
        glEnd();
    }

    private static void drawQuad(Point cornerTopLeft, Point cornerTopRight, Point cornerBottomRight, Point cornerBottomLeft, Color color) {
        glBegin(GL_QUADS);
        glColor3d(color.R, color.G, color.B);
        glVertex2d(cornerTopLeft.X + GLOBAL_HORIZONTAL_DISPLACEMENT, cornerTopLeft.Y + GLOBAL_VERTICAL_DISPLACEMENT);
        glVertex2d(cornerTopRight.X + GLOBAL_HORIZONTAL_DISPLACEMENT, cornerTopRight.Y + GLOBAL_VERTICAL_DISPLACEMENT);
        glVertex2d(cornerBottomRight.X + GLOBAL_HORIZONTAL_DISPLACEMENT, cornerBottomRight.Y + GLOBAL_VERTICAL_DISPLACEMENT);
        glVertex2d(cornerBottomLeft.X + GLOBAL_HORIZONTAL_DISPLACEMENT, cornerBottomLeft.Y + GLOBAL_VERTICAL_DISPLACEMENT);
        glEnd();
    }

    private static void drawThetaSquare(Point start, int width, int height, Color color) {
        Point A = new Point(start.X, start.Y);
        Point B = new Point(start.X + width, start.Y);
        Point C = new Point(start.X + width, start.Y + height);
        Point D = new Point(start.X, start.Y + height);
        drawLine(new Line(A, B, color), 0);
        drawLine(new Line(B, C, color), 0);
        drawLine(new Line(C, D, color), 0);
        drawLine(new Line(D, A, color), 0);
    }

    public static void drawAnyText(String text, float x, float y, float r, float g, float b, float scaleX, float scaleY, int movingPart) {
        ByteBuffer charBuffer = BufferUtils.createByteBuffer(text.length() * 400);
        int firstQuads = stb_easy_font_print(0, 0, text, null, charBuffer);

        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 16, charBuffer);

        glColor3f(r / 255f, g / 255f, b / 255f); // Text color

        glPushMatrix();

        // Zoom
        glScalef(scaleX, scaleY, 1f);
        glTranslatef((x / scaleX) + (GLOBAL_HORIZONTAL_DISPLACEMENT * movingPart), (y / scaleY) + (GLOBAL_VERTICAL_DISPLACEMENT * movingPart), 0f);

        glDrawArrays(GL_QUADS, 0, firstQuads * 4);

        glPopMatrix();
        glEnd();
    }

    public static Point matRotatePoint(Point center, Point point, Double rotation) {
        SimpleMatrix rotMat = getRotationMatrix(rotation);
        SimpleMatrix simpleMatrix = new SimpleMatrix(1, 2, true, new double[]{point.X - center.X, point.Y - center.Y});
        SimpleMatrix result = simpleMatrix.mult(rotMat);
        return new Point(
                Math.toIntExact(Math.round(result.get(0, 0) + center.X)),
                Math.toIntExact(Math.round(result.get(0, 1) + center.Y))
        );
    }

    public static Line matRotateLine(Point center, Line line, Double rotation) {
        SimpleMatrix rotMat = getRotationMatrix(rotation);
        SimpleMatrix point1Matrix = new SimpleMatrix(1, 2, true, new double[]{line.S.X - center.X, line.S.Y - center.Y});
        SimpleMatrix point2Matrix = new SimpleMatrix(1, 2, true, new double[]{line.E.X - center.X, line.E.Y - center.Y});
        SimpleMatrix tmp = point1Matrix.concatRows(point2Matrix);
        SimpleMatrix result = tmp.mult(rotMat);
        return new Line(new Point(
                Math.toIntExact(Math.round(result.get(0, 0) + center.X)),
                Math.toIntExact(Math.round(result.get(0, 1) + center.Y))
        ),
                new Point(
                        Math.toIntExact(Math.round(result.get(1, 0) + center.X)),
                        Math.toIntExact(Math.round(result.get(1, 1) + center.Y))
                ));
    }

    private static SimpleMatrix getRotationMatrix(Double rotation) {
        return new SimpleMatrix(2, 2, true, new double[]{Math.cos(rotation), -Math.sin(rotation), Math.sin(rotation), Math.cos(rotation)});
    }


}
