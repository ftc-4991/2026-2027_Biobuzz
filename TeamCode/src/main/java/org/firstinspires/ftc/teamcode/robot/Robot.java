package org.firstinspires.ftc.teamcode.robot;

import static org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices.gamepadOne;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.pedropathing.math.Pose;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.geometry.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.robot.subsystems.drive.Drive;
import org.firstinspires.ftc.teamcode.robot.subsystems.drive.Pinpoint;
import org.firstinspires.ftc.teamcode.util.logger.tunables.LoggedTunableNumber;

public class Robot extends com.seattlesolvers.solverslib.command.Robot {
    //Subsystems go here
    private final FtcDashboard ftcDashboard;
    private final Drive drive;
    private final Pinpoint pinpoint;

    public enum OpModeType {
        TELEOP, AUTO
    }

    public Robot(OpModeType type) {
        //Initialize subsystems
        ftcDashboard = FtcDashboard.getInstance();
        drive = new Drive();
        pinpoint = new Pinpoint();

        //Register subsystems
        register(
                drive
        );

        initAll();
        if (type == OpModeType.TELEOP) {
            initTele();
        } else {
            initAuto();
        }
    }

    public void initTele() {
        CommandScheduler.getInstance().setDefaultCommand(
                drive,
                drive.drive(
                        () -> gamepadOne.getLeftX(),
                        () -> gamepadOne.getLeftY(),
                        () -> {
                            return gamepadOne.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) - gamepadOne.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER);
                        },
                        () -> !gamepadOne.getButton(GamepadKeys.Button.Y)
                )
        );
        gamepadOne.getGamepadButton(GamepadKeys.Button.LEFT_STICK_BUTTON).whenPressed(new InstantCommand(drive::resetIMU));
    }

    public void initAuto() {
        CommandScheduler.getInstance().schedule(new SequentialCommandGroup(

        ));
    }

    public void initAll() {
        Pose2d startPose = new Pose2d(0,0,0);
        pinpoint.configurePinpoint();
        pinpoint.resetPose(startPose);
        RobotState.getInstance().resetPose(startPose);
    }

    public void loopAll() {
        TelemetryPacket packet = new TelemetryPacket();

        Pose2d pose = RobotState.getInstance().getEstimatedGlobalPose();

        packet.put("x", pose.getX()/ DistanceUnit.mPerInch);
        packet.put("y", pose.getY()/DistanceUnit.mPerInch);
        packet.put("heading (deg)", pose.getRotation().getAngle(AngleUnit.DEGREES));

        ftcDashboard.sendTelemetryPacket(packet);
    }
}
