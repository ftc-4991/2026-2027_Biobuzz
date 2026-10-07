package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

import org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOpMode")
public class TeleOp extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        HardwareDevices.HARDWARE_MAP = hardwareMap;
        HardwareDevices.gamepadOne = new GamepadEx(gamepad1);
        HardwareDevices.gamepadTwo = new GamepadEx(gamepad2);

        Robot robot = new Robot(Robot.OpModeType.TELEOP);

        waitForStart();
        while (opModeIsActive() && !isStopRequested()) {
            robot.run();
        }

        robot.reset();
    }
}
