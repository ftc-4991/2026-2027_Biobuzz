package org.firstinspires.ftc.teamcode.robot.constants;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

import java.lang.Record;

public class HardwareDevices {
    public static HardwareMap HARDWARE_MAP;
    public static GamepadEx gamepadOne;
    public static GamepadEx gamepadTwo;

    public static final String gyroName = "imu";
    public static final IMU.Parameters gyroParameters = new IMU.Parameters(
            new RevHubOrientationOnRobot(
                    RevHubOrientationOnRobot.LogoFacingDirection.UP,
                    RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
            )
    );

    public static final MotorMeta flDrive = new MotorMeta("flDrive", Motor.GoBILDA.RPM_312);
    public static final MotorMeta frDrive = new MotorMeta("frDrive", Motor.GoBILDA.RPM_312);
    public static final MotorMeta blDrive = new MotorMeta("blDrive", Motor.GoBILDA.RPM_312);
    public static final MotorMeta brDrive = new MotorMeta("brDrive", Motor.GoBILDA.RPM_312);

    public static final class MotorMeta {
        public final String motorName;
        public final Motor.GoBILDA goBILDA;

        public MotorMeta(String motorName, Motor.GoBILDA goBILDA) {
            this.motorName = motorName;
            this.goBILDA = goBILDA;
        }
    }
}
