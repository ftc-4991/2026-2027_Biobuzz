package org.firstinspires.ftc.teamcode.autonomous;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

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

public class RedBottom extends OpMode {
    DcMotor fl;
    DcMotor bl;
    DcMotor fr;
    DcMotor br;
    DcMotor intake;
    CRServo leftIntake;
    CRServo rightIntake;
    DcMotor flywheel;
    double flywheelPower = 0.6;
    Servo hood;
    double hoodPosition = 0;
    DcMotor turretMotor; //Rotation
    DcMotor loadingMotor;
    double loaderPower;
    Follower follower;

    PoseFactory p;
    Pose startPose = p.of(60.1,133,270);
    Pose firstEndPose = p.of(57.7, 111.2, 90);
    Pose secondEndPose = p.of(46.3, 128.8, 90);
    @Override
    public void init() {
        fl = hardwareMap.get(DcMotor.class,"frontLeft");
        fr = hardwareMap.get(DcMotor.class,"frontRight");
        bl = hardwareMap.get(DcMotor.class,"backLeft");
        br = hardwareMap.get(DcMotor.class,"backRight");
        intake = hardwareMap.get(DcMotor.class, "topRoller");
        loadingMotor = hardwareMap.get(DcMotor.class, "transferWheel");
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
        loadingMotor.setDirection(DcMotorSimple.Direction.FORWARD);
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
                waitMs(6000),
                Command.build().setStart(() -> {
                    flywheelPower = 0.6; //TODO set power variable to what is necessary for the distance
                    hood.setPosition(hoodPosition); //TODO set the position variable to what is necessary for the distance
                    flywheel.setPower(flywheelPower);

                    //TODO figure out what is necessary for the launch of pollen in the loading mechanism
                    loadingMotor.setPower(loaderPower);

                }),
                waitMs(12000), //TODO put in a reaonsable amount of time because Laedon is mean
                Command.build().setStart(() -> {
                    flywheel.setPower(0);
                    loadingMotor.setPower(0);

                }),

                follow(follower, park()),
                follow(follower, park2())
        );
    }
    private Path park() {
        return line(startPose, firstEndPose).linear(startPose, firstEndPose);
    }
    private Path park2() {
        return line(firstEndPose, secondEndPose).linear(firstEndPose, secondEndPose);
    }
}
