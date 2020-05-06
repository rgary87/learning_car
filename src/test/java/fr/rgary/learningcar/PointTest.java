package fr.rgary.learningcar;

import fr.rgary.learningcar.tracks.JsonToTrack;
import fr.rgary.learningcar.tracks.Track;
import fr.rgary.learningcar.trigonometry.Point;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Class PointTest.
 */
class PointTest {

    @Test
    void track_to_json() throws IOException {
        Track track = JsonToTrack.buildTrack();
        assertEquals(-0.8175d, track.borderOne.get(0).S.X, 0.001d);
    }

    @Test
    void convertMe() {
        Point p1 = new Point(200, 300);
        assertEquals(-0.5, p1.X, 0.001);
        assertEquals(0.5, p1.Y, 0.001);

        Point p2 = new Point(600, 300);
        assertEquals(0.5, p2.X, 0.001);
        assertEquals(0.5, p2.Y, 0.001);

        Point p3 = new Point(400, 600);
        assertEquals(0, p3.X, 0.001);
        assertEquals(0, p3.Y, 0.001);

        Point p4 = new Point(200, 900);
        assertEquals(-0.5, p4.X, 0.001);
        assertEquals(-0.5, p4.Y, 0.001);

        Point p5 = new Point(600, 900);
        assertEquals(0.5, p5.X, 0.001);
        assertEquals(-0.5, p5.Y, 0.001);

        Point p6 = new Point(0, 0);
        assertEquals(-1, p6.X, 0.001);
        assertEquals(1, p6.Y, 0.001);

        Point p7 = new Point(400, 0);
        assertEquals(0, p7.X, 0.001);
        assertEquals(1, p7.Y, 0.001);

        Point p8 = new Point(800, 0);
        assertEquals(1, p8.X, 0.001);
        assertEquals(1, p8.Y, 0.001);

        Point p9 = new Point(0, 1200);
        assertEquals(-1, p9.X, 0.001);
        assertEquals(-1, p9.Y, 0.001);

        Point p11 = new Point(400, 1200);
        assertEquals(0, p11.X, 0.001);
        assertEquals(-1, p11.Y, 0.001);

        Point p12 = new Point(800, 1200);
        assertEquals(1, p12.X, 0.001);
        assertEquals(-1, p12.Y, 0.001);

        Point p13 = new Point(0, 600);
        assertEquals(-1, p13.X, 0.001);
        assertEquals(0, p13.Y, 0.001);

        Point p14 = new Point(800, 600);
        assertEquals(1, p14.X, 0.001);
        assertEquals(0, p14.Y, 0.001);

    }
}
