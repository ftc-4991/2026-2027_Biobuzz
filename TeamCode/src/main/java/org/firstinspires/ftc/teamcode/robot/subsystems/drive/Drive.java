package org.firstinspires.ftc.teamcode.robot.subsystems.drive;

import static org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices.HARDWARE_MAP;
import static org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices.blDrive;
import static org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices.brDrive;
import static org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices.flDrive;
import static org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices.frDrive;

import com.qualcomm.robotcore.hardware.IMU;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.Subsystem;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices;

import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class Drive extends SubsystemBase {
    private final MotorEx fr, fl, br, bl;

    private final IMU gyro;
    private final MecanumDrive mecanumDrive;
    public Drive() {
        fr = new MotorEx(HARDWARE_MAP, frDrive.motorName, frDrive.goBILDA);
        fl = new MotorEx(HARDWARE_MAP, flDrive.motorName, flDrive.goBILDA);
        br = new MotorEx(HARDWARE_MAP, brDrive.motorName, brDrive.goBILDA);
        bl = new MotorEx(HARDWARE_MAP, blDrive.motorName, blDrive.goBILDA);

        mecanumDrive = new MecanumDrive(fl, fr, bl, br);

        gyro = HARDWARE_MAP.get(IMU.class, HardwareDevices.gyroName);
        gyro.initialize(HardwareDevices.gyroParameters);
    }
    @Override
    public void periodic() {

    }

    public void resetIMU() {
        gyro.resetYaw();
    }

    public double getIMUYawRads() {
        return gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
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

    private void drive(double vx, double vy, double omega) {
        drive(vx, vy, omega, true);
    }

    public Command drive(DoubleSupplier vx, DoubleSupplier vy, DoubleSupplier omega, BooleanSupplier driveFieldCentric) {
        final Drive drive = this;
        return new Command() {
            @Override
            public Set<Subsystem> getRequirements() {
                return Set.of(drive);
            }

            @Override
            public void initialize() {

            }

            @Override
            public void execute() {
                drive(vx.getAsDouble(), vy.getAsDouble(), omega.getAsDouble(), driveFieldCentric.getAsBoolean());
            }

            @Override
            public boolean isFinished() {

                return false;
            }
        };
    }

}
