package fr.rgary.learningcar.display;

import fr.rgary.learningcar.Car;
import fr.rgary.learningcar.Processor;
import fr.rgary.learningcar.base.Constant;
import fr.rgary.learningcar.tracks.Track;
import fr.rgary.learningcar.trigonometry.Line;
import fr.rgary.learningcar.trigonometry.Point;
import org.ejml.simple.SimpleMatrix;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.rgary.learningcar.base.Constant.DEBUG_LEVEL;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor3i;
import static org.lwjgl.opengl.GL11.glColor4d;
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

    public static void drawAnyText(String text, float x, float y) {
        Draw.drawAnyText(text, x, y, 169f, 183f, 198f, 1f, 1f);
    }

    public static void drawAnyText(String text, float x, float y, float r, float g, float b, float scaleX, float scaleY) {
        ByteBuffer charBuffer = BufferUtils.createByteBuffer(text.length() * 270);
        int firstQuads = stb_easy_font_print(0, 0, text, null, charBuffer);

        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 16, charBuffer);

        glColor3f(r / 255f, g / 255f, b / 255f); // Text color

        glPushMatrix();

        // Zoom
        glScalef(scaleX, scaleY, 1f);
        glTranslatef(x / scaleX, y / scaleY, 0f);

        glDrawArrays(GL_QUADS, 0, firstQuads * 4);

        glPopMatrix();
    }

    public static void drawTrack(Track track) {
        track.borders.forEach(Draw::drawLine);
        if (DEBUG_LEVEL > 3) {
            track.fitnessZoneLines.forEach(Draw::drawLine);
        }
    }

    private static void drawLine(Line line) {
        glBegin(GL_LINES);
        glColor3d(line.C.R, line.C.G, line.C.B);
        glVertex2d(line.S.X, line.S.Y);
        glVertex2d(line.E.X, line.E.Y);
        glEnd();
    }

    public static void drawPopulation() {
        Processor.POPULATION.forEach(Draw::drawCar);
    }

    private static void drawCar(Car car) {
        if (DEBUG_LEVEL > 2) {
            drawSensors(car);
        }

        Point positionVector = car.position.clone();
        double rotation = car.rotation;
        boolean active = car.active;
        int carLength = Constant.CAR_LENGTH;
        int carWidth = Constant.CAR_WIDTH;

        Point corner_top_left = new Point(positionVector.X - (carWidth / 2), positionVector.Y - (carLength / 2));
        Point corner_top_right = new Point(positionVector.X + (carWidth / 2), positionVector.Y - (carLength / 2));
        Point corner_bottom_right = new Point(positionVector.X + (carWidth / 2), positionVector.Y + (carLength / 2));
        Point corner_bottom_left = new Point(positionVector.X - (carWidth / 2), positionVector.Y + (carLength / 2));

        corner_top_left = matRotatePoint(positionVector, corner_top_left, rotation);
        corner_top_right = matRotatePoint(positionVector, corner_top_right, rotation);
        corner_bottom_right = matRotatePoint(positionVector, corner_bottom_right, rotation);
        corner_bottom_left = matRotatePoint(positionVector, corner_bottom_left, rotation);

        Color c;
        if (active) {
            c = new Color(0, 1, 0);
            glBegin(GL_QUADS);
            glColor3d(c.R, c.G, c.B);
            glVertex2d(corner_top_left.X, corner_top_left.Y);
            glVertex2d(corner_top_right.X, corner_top_right.Y);
            glVertex2d(corner_bottom_right.X, corner_bottom_right.Y);
            glVertex2d(corner_bottom_left.X, corner_bottom_left.Y);
            glEnd();
        } else {
            c = new Color(1, 0, 0, 0.3);
            glBegin(GL_LINES);
            glColor4d(c.R, c.G, c.B, c.A);
            glVertex2d(corner_top_left.X, corner_top_left.Y);
            glVertex2d(corner_top_right.X, corner_top_right.Y);
            glVertex2d(corner_top_right.X, corner_top_right.Y);
            glVertex2d(corner_bottom_right.X, corner_bottom_right.Y);
            glVertex2d(corner_bottom_right.X, corner_bottom_right.Y);
            glVertex2d(corner_bottom_left.X, corner_bottom_left.Y);
            glVertex2d(corner_bottom_left.X, corner_bottom_left.Y);
            glVertex2d(corner_top_left.X, corner_top_left.Y);
            glEnd();
        }
    }

    private static void drawSensors(Car car) {
        glBegin(GL_LINES);
        if (car.active) {
            glColor3d(0/255d, 0/255d, 255/255d);
        } else {
            glColor3d(110/255d, 255/255d, 255/255d);
        }
        for (int i = 0; i < car.sensorIntersectPoints.length; i++) {
            if (car.sensorIntersectPoints[i] == null) {
                continue;
            }
            glVertex2d(car.position.X, car.position.Y);
            glVertex2d(car.sensorIntersectPoints[i].X, car.sensorIntersectPoints[i].Y);
        }
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
