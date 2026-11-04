package org.firstinspires.ftc.teamcode.robot;

import static org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices.HARDWARE_MAP;
import static org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices.blDrive;
import static org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices.brDrive;
import static org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices.flDrive;
import static org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices.frDrive;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.controllers.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.Subsystem;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices;
import org.firstinspires.ftc.teamcode.robot.subsystems.drive.Drive;

import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

@Config
@TeleOp
public class pidSmth extends LinearOpMode {
    public static double P = 0.0;
    public static double I = 0.0;
    public static double D = 0.0;

    MotorEx fr, fl, br, bl;
    IMU gyro;
    MecanumDrive mecanumDrive;

    @Override
    public void runOpMode() throws InterruptedException {
        fr = new MotorEx(hardwareMap, frDrive.motorName, frDrive.goBILDA);
        fl = new MotorEx(hardwareMap, flDrive.motorName, flDrive.goBILDA);
        br = new MotorEx(hardwareMap, brDrive.motorName, brDrive.goBILDA);
        bl = new MotorEx(hardwareMap, blDrive.motorName, blDrive.goBILDA);

        mecanumDrive = new MecanumDrive(fl, fr, bl, br);

        gyro = hardwareMap.get(IMU.class, HardwareDevices.gyroName);
        gyro.initialize(HardwareDevices.gyroParameters);

        // 2. Initialize the PIDController *outside* the loop once
        PIDController pidController = new PIDController(P, I, D);

        waitForStart();

        while (opModeIsActive()) {
            double error = 0 - getIMUYawDegs();
            while (error > 180) error -= 360;
            while (error < -180) error += 360;

            telemetry.addData("Running PID Command with P", P);
            telemetry.update();

            drive(0, 0, pidController.calculate(0, error));
        }
    }

    private void drive(double vx, double vy, double omega) {
        drive(vx, vy, omega, true);
    }

    public double getIMUYawDegs() {
        return gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    private void drive(double vx, double vy, double omega, boolean fieldCentric) {
        if (fieldCentric) {
            mecanumDrive.driveFieldCentric(vx, vy, omega, getIMUYawDegs());
        } else {
            mecanumDrive.driveRobotCentric(vx, vy, omega);
        }
    }
}