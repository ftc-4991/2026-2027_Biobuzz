package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

@TeleOp
public class turretBoundaryTest extends LinearOpMode {
    DcMotor rotMotor = null;
    MotorEx flywheel = null;
    ServoEx hood = null;

    double currentRotation;
    double targetRotation;

    double highLimit;
    double lowLimit;


    @Override
    public void runOpMode() throws InterruptedException {
        flywheel = new MotorEx(hardwareMap, "flyWheelRoller");
        rotMotor = hardwareMap.get(DcMotor.class, "turret horizontal rotation motor");
        hood = new ServoEx(hardwareMap, "hood");
        rotMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rotMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        waitForStart();
        while (opModeIsActive()) {
            currentRotation = rotMotor.getCurrentPosition();
            targetRotation = rotMotor.getTargetPosition();



            telemetry.addData("Turret Rotation: ", currentRotation);
            telemetry.addData("Target Rotation: ", targetRotation);
            telemetry.update();
        }
    }
}
