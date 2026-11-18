package org.firstinspires.ftc.teamcode.teleop;

//Sophia Schetz
//11/12/2026
//BioBuzz 2026-2027

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="TelleOpSimplified")
public class TeleOpSimplified extends LinearOpMode {
    //Creating the Motors
    DcMotor frontLeft;
    DcMotor backLeft;
    DcMotor frontRight;
    DcMotor backRight;
    DcMotor topRoller;  //Top wheels of the Intake
    CRServo leftRoller; //Left wheel of the Intake
    CRServo rightRoller; //Right wheel of the Intake
    DcMotor flyWheelRoller; //Wheel for the Turret
    Servo flyWheelShield;  //Hood of the Turret
    DcMotor turretRotationalMotor;  //Motor to rotate the Turret
    DcMotor transferWheel;  //Wheel to take the pollen from the intake to the turret

    //Creating the Booleans
    //double flyWheelRotation;
    //double currentTurretRotation;
    //double targetRotation = 0;
    //double minimumRotation;
   // double maximumRotation;
    double flyWheelRollerPower = 0;
    double flyWheelShieldPosition = 0;
    double frontLeftPower;
    double frontRightPower;
    double backLeftPower;
    double backRightPower;

    //Control for the intake motors
    //boolean intake;
    //boolean reject;

    //Loop for when the code is initiated
    @Override
    public void runOpMode() throws InterruptedException {

        //Creating the Motors and Servos in the hardware map
        frontLeft = hardwareMap.get(DcMotor.class,"frontLeft");
        frontRight = hardwareMap.get(DcMotor.class,"frontRight");
        backLeft = hardwareMap.get(DcMotor.class,"backLeft");
        backRight = hardwareMap.get(DcMotor.class,"backRight");
        topRoller = hardwareMap.get(DcMotor.class, "topRoller");
        leftRoller = hardwareMap.get(CRServo.class, "leftRoller");
        rightRoller = hardwareMap.get(CRServo.class, "rightRoller");
        flyWheelRoller = hardwareMap.get(DcMotor.class, "flyWheelRoller");
        flyWheelShield = hardwareMap.get(Servo.class, "hood");
        turretRotationalMotor = hardwareMap.get(DcMotor.class, "turret horizontal rotation motor");
        transferWheel=hardwareMap.get(DcMotor.class,"transferWheel");

        //Choosing how the motor runs at default
        turretRotationalMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Setting the brake variable
        turretRotationalMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Setting the motor's directions
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        topRoller.setDirection(DcMotorSimple.Direction.FORWARD);
        leftRoller.setDirection(DcMotorSimple.Direction.REVERSE);
        rightRoller.setDirection(DcMotorSimple.Direction.FORWARD);
        flyWheelRoller.setDirection(DcMotorSimple.Direction.FORWARD);
        turretRotationalMotor.setDirection(DcMotor.Direction.FORWARD);
        transferWheel.setDirection(DcMotorSimple.Direction.REVERSE);

        //Setting the fly wheel shield initial position
        flyWheelShieldPosition = 0.5;
        flyWheelShield.setPosition(0.5);

        //Creating the variables for intake
        boolean isIntaking = false;

        //Creating the variable for reject
        boolean isRejecting = false;

        //Initializing the Motors
        waitForStart();
        //Loop for driving robot and controlling motor.
        while (opModeIsActive()) {

            //Control the robots basic x and y axis movement and rotation(theta)
            double y = gamepad1.left_stick_y;
            y = y * Math.abs(y);
            double x = -gamepad1.left_stick_x;
            x = x * Math.abs(x);
            double rotation = -gamepad1.right_stick_x;
            rotation = rotation * Math.abs(rotation);

            //Creating more booleans for the basic field movement
            frontLeftPower = y + x + rotation;
            frontRightPower = y - x - rotation;
            backLeftPower = y - x + rotation;
            backRightPower = y + x - rotation;

            //Setting the Powers to the Servos
            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);
            flyWheelRoller.setPower(flyWheelRollerPower);
            //flyWheelRotationalMotor.setPower(flyWheelRotation);

            //Toggle Controls for the Intake
            if(gamepad1.rightBumperWasPressed()) {
                if (isIntaking) {
                    isIntaking = false;
                    topRoller.setPower(0);
                    leftRoller.setPower(0);
                    rightRoller.setPower(0);
                } else {
                    isIntaking = true;
                    topRoller.setPower(1);
                    leftRoller.setPower(1);
                    rightRoller.setPower(1);
                }
            }

            //Toggle Controls for the Reject
            if(gamepad1.leftBumperWasPressed()){
                if(isRejecting){
                isRejecting = false;
                topRoller.setPower(0);
                leftRoller.setPower(0);
                rightRoller.setPower(0);
            } else {
                isRejecting = true;
                topRoller.setPower(-1);
                leftRoller.setPower(-1);
                rightRoller.setPower(-1);
            }
                } //end tag for if(isRejecting)
            if (gamepad1.bWasPressed()){
                topRoller.setPower(0);
            }

            //Setting the Fly Wheel adjustable speed
            if (gamepad2.dpadDownWasPressed()){
                flyWheelRollerPower -= 0.05;
            }
            if (gamepad2.dpadUpWasPressed()){
                flyWheelRollerPower += 0.05;
            }
            //Turning the Fly Wheel off
            if (gamepad2.yWasPressed()){
                flyWheelRollerPower=(0);
            }
            //The Fly wheels basic speed
            if (gamepad2.aWasPressed()){
                flyWheelRollerPower=(0.6);
            }
            //Setting the Fly Wheel Hood Position
            if (gamepad2.dpadLeftWasPressed()){
                flyWheelShieldPosition -= 0.05;
                flyWheelShield.setPosition(flyWheelShieldPosition);
            }
            if (gamepad2.dpadRightWasPressed()) {
                flyWheelShieldPosition += 0.05;
                flyWheelShield.setPosition(flyWheelShieldPosition);
            }

            //Setting the Turret Rotation
            /* if(gamepad2.xWasPressed()){
                if(currentTurretRotation - 500 >= minimumRotation) {
                    targetRotation = currentTurretRotation - 500;
                } else {
                    targetRotation = minimumRotation;
                }
                flyWheelRotationalMotor.setTargetPosition((int)targetRotation);
                flyWheelRotationalMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sleep(500);
                flyWheelRotationalMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            if(gamepad2.bWasPressed()) {
                if (currentTurretRotation + 500 <= maximumRotation) {
                    targetRotation = currentTurretRotation + 500;
                } else {
                    targetRotation = maximumRotation;
                }
                flyWheelRotationalMotor.setTargetPosition((int) targetRotation);
                flyWheelRotationalMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sleep(500);
                flyWheelRotationalMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            */
                //Setting the Transfer Wheel actions
                if(gamepad2.left_bumper){
                    transferWheel.setPower(0.5);
                }while(gamepad2.right_bumper){
                    transferWheel.setPower(0);
                }

            //Defining the Turret's Variables
            //currentTurretRotation = (flyWheelRotationalMotor.getCurrentPosition());

            //Creating and Setting the Telemetry
            telemetry.addData("Fly Wheel Speed", flyWheelRollerPower);
            telemetry.addData("Fly Wheel Hood Position", flyWheelShieldPosition);
            telemetry.update();
        }

    }
}

