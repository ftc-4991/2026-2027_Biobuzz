package org.firstinspires.ftc.teamcode.teleop;

//Sophia Schetz
//11/17/2026
//BioBuzz 2026-2027

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="TeleOpMain")
public class TeleOpMain extends LinearOpMode {
    //Creating the Motors
    DcMotor frontLeft;
    DcMotor backLeft;
    DcMotor frontRight;
    DcMotor backRight;
    DcMotor topRoller;  //Top wheels of the Intake
    CRServo leftRoller; //Left wheel of the Intake
    CRServo rightRoller; //Right wheel of the Intake
    DcMotor flyWheelRoller; //Wheel for the Turret
    Servo flyWheelHood;  //Hood of the Turret
    DcMotor turretRotationalMotor;  //Motor to rotate the Turret
    DcMotor transferWheel;  //Wheel to take the pollen from the intake to the turret

    //Creating the Booleans
    //double flyWheelRotation;
    //double currentTurretRotation;
    //double targetRotation = 0;
    //double minimumRotation;
   // double maximumRotation;
    double flyWheelRollerPower = 0;
    double flyWheelHoodPosition = 0;
    double frontLeftPower;
    double frontRightPower;
    double backLeftPower;
    double backRightPower;
    //Creating the variables for intake
    boolean isIntaking = false;

    //Creating the variable for reject
    boolean isRejecting = false;

    //Control for the intake motors
    //boolean intake;
    //boolean reject;

    /**
     * setting all the motors in servos in the hardware map.
     */
    public void lookupHardware() {
        //Creating the Motors and Servos in the hardware map
        frontLeft = hardwareMap.get(DcMotor.class,"frontLeft");
        frontRight = hardwareMap.get(DcMotor.class,"frontRight");
        backLeft = hardwareMap.get(DcMotor.class,"backLeft");
        backRight = hardwareMap.get(DcMotor.class,"backRight");
        topRoller = hardwareMap.get(DcMotor.class, "topRoller");
        leftRoller = hardwareMap.get(CRServo.class, "leftRoller");
        rightRoller = hardwareMap.get(CRServo.class, "rightRoller");
        flyWheelRoller = hardwareMap.get(DcMotor.class, "flyWheelRoller");
        flyWheelHood = hardwareMap.get(Servo.class, "hood");
        turretRotationalMotor = hardwareMap.get(DcMotor.class, "turret horizontal rotation motor");
        transferWheel=hardwareMap.get(DcMotor.class,"transferWheel");

    }

    /**
     * Setting how the encoders run, the brake variable, and motor directions.
     */
    public void initializeHardware(){
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
        flyWheelHoodPosition = 0.5;
        flyWheelHood.setPosition(0.5);

    }

    /**
     * Setting the controls for the wheels and setting the wheels power.
     */
    public void wheels(){
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
    }

    /**
     * Setting the variables for the powers of motors.
     */
     public void settingPowers(){
         //Setting the Powers to the Servos
         frontLeft.setPower(frontLeftPower);
         frontRight.setPower(frontRightPower);
         backLeft.setPower(backLeftPower);
         backRight.setPower(backRightPower);
         flyWheelRoller.setPower(flyWheelRollerPower);
     }

    /**
     * The loops for intake and reject.
     */
    public void intakeControls(){
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
     }

    /**
     * Controls for the fly wheel
     */
    public void flyWheelControls(){
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
     }

    /**
     * Setting the controls for the fly wheel hood movement.
     */
    public void flyWheelHoodControls(){
         //Setting the Fly Wheel Hood Position
         if (gamepad2.dpadLeftWasPressed()){
             flyWheelHoodPosition -= 0.05;
             flyWheelHood.setPosition(flyWheelHoodPosition);
         }
         if (gamepad2.dpadRightWasPressed()) {
             flyWheelHoodPosition += 0.05;
             flyWheelHood.setPosition(flyWheelHoodPosition);
         }
     }

    /**
     * Setting the movement for the turret rotation.
     */
    public void turretRotationControls(){
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
     }
     /**
     * Controls for the transfer wheel.
     */
    public void transferWheelControls(){
         //Setting the Transfer Wheel actions
         if(gamepad2.left_bumper){
             transferWheel.setPower(0.5);
         }while(gamepad2.right_bumper){
             transferWheel.setPower(0);
         }
     }

    /**
     * Updating the telemetry.
     */
    public void updateAllTelemetry(){
         //Creating and Setting the Telemetry
         telemetry.addData("Fly Wheel Speed", flyWheelRollerPower);
         telemetry.addData("Fly Wheel Hood Position", flyWheelHoodPosition);
         telemetry.update();
     }

    //Loop for when the code is initiated
    @Override
    public void runOpMode() throws InterruptedException {

        lookupHardware();

        initializeHardware();

        //Initializing the Motors
        waitForStart();
        //Loop for driving robot and controlling motor.
        while (opModeIsActive()) {

           wheels();

           intakeControls();

           flyWheelControls();

           flyWheelHoodControls();

           turretRotationControls();

           transferWheelControls();

            settingPowers();

           updateAllTelemetry();

        }

    }
}

