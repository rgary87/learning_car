package fr.rgary.learningcar.trigonometry;

/**
 * Class Point.
 */
public class Point {
    public int X;
    public int Y;

    public double carX;
    public double carY;

    private Point() {
    }

    public Point(int x, int y) {
        this.X = x;
        this.Y = y;
        this.carX = X;
        this.carY = Y;
    }

    public Point(double carX, double carY) {
        this.carX = carX;
        this.carY = carY;
        this.X = Math.toIntExact(Math.round(carX));
        this.Y = Math.toIntExact(Math.round(carY));
    }

    public Point(int x, int y, double carX, double carY) {
        this.X = x;
        this.Y = y;
        this.carX = carX;
        this.carY = carY;
    }

    public Point clone() {
        return new Point(X, Y, carX, carY);
    }

    public Point moveMe(int step) {
        this.Y += step;
        this.carY += step;
        return this;
    }

    @Override
    public String toString() {
        return  "{ \"X\": "+X+", \"Y\": "+Y+", \"carX\": "+carX+", \"carY\": "+carY+" }";
    }
}

