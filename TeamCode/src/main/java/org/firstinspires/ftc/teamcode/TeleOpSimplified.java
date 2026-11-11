package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

//Name the code and Define what type of code it is
@TeleOp(name="TeleOpSimplified")
public class TeleOpSimplified extends LinearOpMode {
    //Define Motors and their Variable Names
    DcMotor frontLeft;
    DcMotor backLeft;
    DcMotor frontRight;
    DcMotor backRight;
    DcMotor topRoller;
    CRServo leftRoller;
    CRServo rightRoller;
    DcMotor flyWheelRoller;
    double flyWheelRollerPower = 0;
    Servo flyWheelShield;
    double flyWheelShieldPosition = 0;
    DcMotor flyWheelRotationalMotor;

    double flyWheelRotation;
    double currentTurretRotation;
    double targetRotation = 0;
    double minimumRotation;
    double maximumRotation;


    @Override
    public void runOpMode() throws InterruptedException {

        //find the on the hardware map to define the variables
       frontLeft = hardwareMap.get(DcMotor.class,"frontLeft");
        frontRight = hardwareMap.get(DcMotor.class,"frontRight");
        backLeft = hardwareMap.get(DcMotor.class,"backLeft");
        backRight = hardwareMap.get(DcMotor.class,"backRight");
        topRoller = hardwareMap.get(DcMotor.class, "topRoller");
        leftRoller = hardwareMap.get(CRServo.class, "leftRoller");
        rightRoller = hardwareMap.get(CRServo.class, "rightRoller");
        flyWheelRoller = hardwareMap.get(DcMotor.class, "flyWheelRoller");
        flyWheelShield = hardwareMap.get(Servo.class, "hood");
        flyWheelRotationalMotor = hardwareMap.get(DcMotor.class, "turret horizontal rotation motor");

        //Chosing how the motor runs
        flyWheelRotationalMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        flyWheelRotationalMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Motor Directions
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        topRoller.setDirection(DcMotorSimple.Direction.FORWARD);
        leftRoller.setDirection(DcMotorSimple.Direction.REVERSE);
        rightRoller.setDirection(DcMotorSimple.Direction.FORWARD);
        flyWheelRoller.setDirection(DcMotorSimple.Direction.FORWARD);
        flyWheelRotationalMotor.setDirection(DcMotor.Direction.FORWARD);

        //Setting the fly wheel shield initial position
        flyWheelShieldPosition = 0.4;

        //Variables for intake
        boolean isIntaking = false;

        //Variable for Reject
        boolean isRejecting = false;

        //Loop for Driving Robot and Controlling motor.
        waitForStart();
        while (opModeIsActive()) {

            //Control the robots basic x and y axis movement and rotation
            double y = gamepad1.left_stick_y;
            y = y * Math.abs(y);
            double x = -gamepad1.left_stick_x;
            x = x * Math.abs(x);
            double rotation = -gamepad1.right_stick_x;
            rotation = rotation * Math.abs(rotation);

            //Control Intake Motors
            boolean intake = gamepad1.right_bumper;

            //Make Reject variables
            boolean reject = gamepad1.left_bumper;

            //Making motors go faster
            double frontLeftPower = y + x + rotation;
            double frontRightPower = y - x - rotation;
            double backLeftPower = y - x + rotation;
            double backRightPower = y + x - rotation;

            //Setting the Powers
            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);
            flyWheelRoller.setPower(flyWheelRollerPower);
            flyWheelRotationalMotor.setPower(flyWheelRotation);

            //Toggle for the intake
            if(gamepad1.right_bumper && isIntaking){
                isIntaking = false;
                topRoller.setPower(0);
                leftRoller.setPower(0);
                rightRoller.setPower(0);
            } else if (intake && !isIntaking){
                isIntaking = true;
                topRoller.setPower(1);
                leftRoller.setPower(1);
                rightRoller.setPower(1);
            }

            //Toggle for the reject
            if(gamepad1.left_bumper && isRejecting){
                isRejecting = false;
                topRoller.setPower(0);
                leftRoller.setPower(0);
                rightRoller.setPower(0);
            } else if (reject && !isRejecting){
                isRejecting = true;
                topRoller.setPower(-1);
                leftRoller.setPower(-1);
                rightRoller.setPower(-1);
            }

            //Fly Wheel Speed
            if (gamepad1.dpadDownWasPressed()){
                flyWheelRollerPower -= 0.05;
            }
            if (gamepad1.dpadUpWasPressed()){
                flyWheelRollerPower += 0.05;
            }
            if (gamepad1.yWasPressed()){
                flyWheelRoller.setPower(0);
            }
            if (gamepad1.dpadLeftWasPressed()){
                flyWheelShieldPosition -= 0.05;
                flyWheelShield.setPosition(flyWheelShieldPosition);

            }
            if (gamepad1.dpadRightWasPressed()) {
                flyWheelShieldPosition += 0.05;
                flyWheelShield.setPosition(flyWheelShieldPosition);
            }
            if(gamepad1.xWasPressed()){
                if (currentTurretRotation - 500 >= minimumRotation) {
                    targetRotation = currentTurretRotation - 500;
                } else {
                    targetRotation = minimumRotation;
                }
                flyWheelRotationalMotor.setTargetPosition((int)targetRotation);
                flyWheelRotationalMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sleep(500);
                flyWheelRotationalMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            if(gamepad1.bWasPressed()){
                if (currentTurretRotation + 500 <= maximumRotation) {
                    targetRotation = currentTurretRotation + 500;
                } else {
                    targetRotation = maximumRotation;
                }
                flyWheelRotationalMotor.setTargetPosition((int)targetRotation);
                flyWheelRotationalMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sleep(500);
                flyWheelRotationalMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            currentTurretRotation = (flyWheelRotationalMotor.getCurrentPosition());


            //telemetry
            telemetry.addData("Fly Wheel Speed", flyWheelRollerPower);
            telemetry.addData("Fly Wheel Hood Position", flyWheelShieldPosition);
            telemetry.update();
        }

    }
}

