package fr.rgary.learningcar.trigonometry;

import java.awt.*;

/**
 * Class Zone.
 */
public class Zone {
    public Polygon polygon;
    public int zoneNumber;
    public Point center;

    public Zone(int zoneNumber, Point p1, Point p2, Point p3, Point p4) {
        this.zoneNumber = zoneNumber;
        this.polygon = new Polygon();
        this.polygon.addPoint(p1.X + 1_000_000, p1.Y + 1_000_000);
        this.polygon.addPoint(p2.X + 1_000_000, p2.Y + 1_000_000);
        this.polygon.addPoint(p3.X + 1_000_000, p3.Y + 1_000_000);
        this.polygon.addPoint(p4.X + 1_000_000, p4.Y + 1_000_000);
        int lowerX = 100000;
        int lowerY = 100000;
        int maxX = -100000;
        int maxY = -100000;
        if (p1.X  < lowerX)
            lowerX = p1.X ;
        if (p2.X  < lowerX)
            lowerX = p2.X ;
        if (p3.X  < lowerX)
            lowerX = p3.X ;
        if (p4.X  < lowerX)
            lowerX = p4.X ;

        if (p1.Y < lowerY)
            lowerY = p1.Y;
        if (p2.Y < lowerY)
            lowerY = p2.Y;
        if (p3.Y < lowerY)
            lowerY = p3.Y;
        if (p4.Y < lowerY)
            lowerY = p4.Y;

        if (p1.X  > maxX)
            maxX = p1.X ;
        if (p2.X  > maxX)
            maxX = p2.X ;
        if (p3.X  > maxX)
            maxX = p3.X ;
        if (p4.X  > maxX)
            maxX = p4.X ;

        if (p1.Y > maxY)
            maxY = p1.Y;
        if (p2.Y > maxY)
            maxY = p2.Y;
        if (p3.Y > maxY)
            maxY = p3.Y;
        if (p4.Y > maxY)
            maxY = p4.Y;

        this.center = new Point(lowerX + ((maxX - lowerX) / 2), lowerY + ((maxY - lowerY) / 2));
    }

}
