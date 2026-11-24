package org.firstinspires.ftc.teamcode;

public class Point {
    private double x;
    private double y;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }
    public double getY() {
        return this.y;
    }

    /**
     * Put the larger number first for a positive result
     *
     * @param x1 represents 'x2' in 'x2-x1'
     * @param x2 subtracted from x1
     * @return returns the distance between two values
     */
    public double getDistance(int x1, int x2) {
        return Math.abs(x1 - x2);
    }

    /**
     * Put the point with higher values first if you want a positive result
     *
     * @param p1 represents (x2, y2) in the distance formula
     * @param p2 represents (x1, y1) in the distance formula
     * @return returns the distance between 2 points
     */
    public double getDistance(Point p1, Point p2) {
        return Math.sqrt((Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2)));
    }
}
