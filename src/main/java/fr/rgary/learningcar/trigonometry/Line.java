package fr.rgary.learningcar.trigonometry;

import fr.rgary.learningcar.display.Color;

import java.util.Optional;

/**
 * Class Line.
 */
public class Line {
    public Point S;
    public Point E;
    public Color C = new Color(1, 0, 0);

    private Line() {
    }

    public Line(Point s, Point e) {
        S = s;
        E = e;
        C = new Color(1, 0, 0);
    }

    public Line(Point s, Point e, Color c) {
        S = s;
        E = e;
        C = c;
    }

    public static Point calculateIntersectionPoint(Line line1, Line line2) {
        double s1_x = line1.E.X - line1.S.X;
        double s1_y = line1.E.Y - line1.S.Y;
        double s2_x = line2.E.X - line2.S.X;
        double s2_y = line2.E.Y - line2.S.Y;

        double s = (-s1_y * (line1.S.X - line2.S.X) + s1_x * (line1.S.Y - line2.S.Y)) / (-s2_x * s1_y + s1_x * s2_y);
        double t = (s2_x * (line1.S.Y - line2.S.Y) - s2_y * (line1.S.X - line2.S.X)) / (-s2_x * s1_y + s1_x * s2_y);

        if (0 <= s && s <= 1 && 0 <= t && t <= 1) {
            int i_x = Math.toIntExact(Math.round(line1.S.X + (t * s1_x)));
            int i_y = Math.toIntExact(Math.round(line1.S.Y + (t * s1_y)));
            return new Point(i_x, i_y);
        }
        return null;
    }

    @Override
    public String toString() {
        return "{\"S\":" + S.toString() + ", \"E\":" + E.toString() + '}';
    }
}
