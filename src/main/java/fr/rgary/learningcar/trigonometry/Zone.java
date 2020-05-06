package fr.rgary.learningcar.trigonometry;

import java.awt.*;

/**
 * Class Zone.
 */
public class Zone {
    public Polygon polygon;
    public int zoneNumber;

    public Zone(int zoneNumber, Point p1, Point p2, Point p3, Point p4) {
        this.zoneNumber = zoneNumber;
        this.polygon = new Polygon();
        this.polygon.addPoint(p1.X, p1.Y);
        this.polygon.addPoint(p2.X, p2.Y);
        this.polygon.addPoint(p3.X, p3.Y);
        this.polygon.addPoint(p4.X, p4.Y);
    }

}
