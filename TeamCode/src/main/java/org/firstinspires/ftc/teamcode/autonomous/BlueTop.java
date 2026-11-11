package org.firstinspires.ftc.teamcode.autonomous;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import static java.lang.Thread.sleep;
import static java.util.concurrent.locks.LockSupport.park;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

public class BlueTop extends OpMode {
    DcMotor fl;
    DcMotor bl;
    DcMotor fr;
    DcMotor br;
    DcMotor intake;
    CRServo leftIntake;
    CRServo rightIntake;
    DcMotor flywheel;
    double flywheelPower = 0;
    Servo hood;
    double hoodPosition = 0;
    DcMotor turretMotor; //Rotation
    Follower follower;

    PoseFactory p;
    Pose startPose = p.of(83,133,270);
    Pose endPose = p.of(129.5, 94.3, 0);
    @Override
    public void init() {
        fl = hardwareMap.get(DcMotor.class,"frontLeft");
        fr = hardwareMap.get(DcMotor.class,"frontRight");
        bl = hardwareMap.get(DcMotor.class,"backLeft");
        br = hardwareMap.get(DcMotor.class,"backRight");
        intake = hardwareMap.get(DcMotor.class, "topRoller");
        leftIntake = hardwareMap.get(CRServo.class, "leftRoller");
        rightIntake = hardwareMap.get(CRServo.class, "rightRoller");
        flywheel = hardwareMap.get(DcMotor.class, "flyWheelRoller");
        hood = hardwareMap.get(Servo.class, "hood");
        turretMotor = hardwareMap.get(DcMotor.class, "turret horizontal rotation motor");

        fl.setDirection(DcMotorSimple.Direction.FORWARD);
        fr.setDirection(DcMotorSimple.Direction.FORWARD);
        bl.setDirection(DcMotorSimple.Direction.FORWARD);
        br.setDirection(DcMotorSimple.Direction.REVERSE);
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        leftIntake.setDirection(DcMotorSimple.Direction.REVERSE);
        rightIntake.setDirection(DcMotorSimple.Direction.FORWARD);
        flywheel.setDirection(DcMotorSimple.Direction.FORWARD);
        turretMotor.setDirection(DcMotor.Direction.FORWARD);
        Scheduler.reset();
        follower = Constants.createFollower(hardwareMap);
        follower.setPose(startPose);
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
    }

    private Command autoRoutine() {
        return sequential(

                Command.build().setStart(() -> {
                    flywheelPower = 0.9; //TODO set power variable to what is necessary for the distance
                    hood.setPosition(hoodPosition); //TODO set the position variable to what is necessary for the distance
                    flywheel.setPower(flywheelPower);

                    //TODO figure out what is necessary for the launch of pollen in the loading mechanism


                }),
                waitMs(12000), //TODO put in a reaonsable amount of time because Laedon is mean
                Command.build().setStart(() -> {
                    flywheelPower = 0;
                    flywheel.setPower(flywheelPower);
                }),

                follow(follower, park())
        );
    }
    private Path park() {
        return line(startPose, endPose).linear(startPose, endPose);
    }
}
