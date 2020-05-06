package fr.rgary.learningcar.trigonometry;

/**
 * Class Point.
 */
public class Point {
    public int X;
    public int Y;

    private Point() {
    }

    public Point(int x, int y) {
        X = x;
        Y = y;
    }

    public Point clone() {
        return new Point(X, Y);
    }

    public Point moveMe(int step) {
        this.Y += step;
        return this;
    }

}
