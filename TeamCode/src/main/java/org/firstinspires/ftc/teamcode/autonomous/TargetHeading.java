package org.firstinspires.ftc.teamcode.autonomous;


import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.Point;

public class TargetHeading {

    public static final Point BLUE_TOP_HIVE = new Point(82.542, 82.542);
    public static final Point BLUE_BOTTOM_HIVE = new Point(82.542, 58.958);
    public static final Point RED_TOP_HIVE = new Point (58.958, 82.542);
    public static final Point RED_BOTTOM_HIVE = new Point (58.958, 58.958);
    private Point robotPoint; // may be redundant
    private Point cornerPoint; //should use the x value of the robotPoint and the Y value of the hive point

    private double currentHeading;
    private double targetHeading;
    private double hypotenuseDistance;
    private double xDistance;
    private double yDistance;

    private double upperAngleLimit;
    private double lowerAngleLimit;


    private Follower follower;

    private double robotX;
    private double robotY;
    private double robotHeading;
    /**
     *
     * separate the field into quadrants (used for trig) the origin of the quadrants should be
     * based around the center of the hive that is being targeted. Targeted hives are based on the
     * y value of the robot (to determine the 'top' and 'bottom' hive to target) and the alliance
     *
     *
     * determine which alliance the robot is on
     *
     * put current robot position as a point (May need separate class to hold the data)
     * determine which quadrants should be
     *  if the robot is on blue alliance, the relevant quadrants should be 1 and 4
     *  if the robot is on red alliance, the relevant quadrants should be 2 and 3
     *
     * determine all distances between the robot and  the ccenter of the targeted hive
     *  one corner point should be the coordinates of the hive
     *  one corner point should be the coordinates of the robot's position
     *  one corner point should be the x coordinate of the hive and the y coordinate of the robot
     *
     * retrieve the raw target angle for the robot
     *  in quadrant 1 and 2, use arcsin; opposite to theta / hypotenuse
     *  in quadrant 3 use arctan;        opposite to theta / adjacent to theta
     *  in quadrant 4 use arccos;        adjacent to theta / hypotenuse
     *
     * determine if the value of the target heading is valid
     *  the heading will be based on a 180 degree arc about the robot's heading
     *  get the robot's current heading (pose.heading or similar)
     *  add 90 to the robot's heading for the upper limit
     *  subtract 90 to the robot's heading for the lower limit
     *  normalize the value within 360 degrees using % (mod)
     *   if the value of either limit is negative, add 360 to it
     *  compare the target heading with the limits; lowerLimit < targetHeading < upperLimit
     *  if true, return true
     *  if false, break out of the method
     *  //TODO determine edge cases such as the robot trying to use the wrong arc
     *
     * convert between ticks and degrees
     *  add a method to convert from ticks to degrees
     *  add a mehod to convert from degrees to ticks
     *
     * create a method to determine a valid target heading and return the # of ticks to it
     *  get target heading
     *  determine if the heading is valid
     *  convert the target heading to ticks
     *  return ticks
     *
     * create a method to give back the upper limit angle
     *  get robot's heading
     *  return robot's heading plus 90
     *  if robot's heading + 90 > 360, subtract 360 OR use % 360
     *
     * create a method to give back the lower limit angle
     *  get robot's heading
     *  return robot's heading minus 90
     *  if robot's heading - 90 < 0, add 360 or use % -360
     *
     * 
     *
     */




}
