package org.firstinspires.ftc.teamcode.robot;

import static org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices.gamepadOne;

import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.robot.subsystems.drive.Drive;

public class Robot extends com.seattlesolvers.solverslib.command.Robot {
    //Subsystems go here
    private final Drive drive;
    public enum OpModeType {
        TELEOP, AUTO
    }

    public Robot(OpModeType type) {
        //Initialize subsystems
        drive = new Drive();

        //Register subsystems
        register(
                drive
        );


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
}
