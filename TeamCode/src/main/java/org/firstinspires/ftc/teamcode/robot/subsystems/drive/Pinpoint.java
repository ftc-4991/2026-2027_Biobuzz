package org.firstinspires.ftc.teamcode.robot.subsystems.drive;

import static org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices.HARDWARE_MAP;
import static org.firstinspires.ftc.teamcode.robot.constants.HardwareDevices.pinpointName;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.geometry.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.robot.Robot;
import org.firstinspires.ftc.teamcode.robot.RobotState;

public class Pinpoint extends SubsystemBase {
    private final GoBildaPinpointDriver pinpoint;
    private final FtcDashboard ftcDashboard;

    public Pinpoint() {
        pinpoint = HARDWARE_MAP.get(GoBildaPinpointDriver.class, pinpointName);
        ftcDashboard = FtcDashboard.getInstance();
    }

    public void configurePinpoint() {
        pinpoint.setOffsets(-1.4389742453267256, -7.490844125822774, DistanceUnit.INCH);

        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        pinpoint.resetPosAndIMU();
    }

    public void resetPose(Pose2d pose) {
        pinpoint.setPosition(new Pose2D(DistanceUnit.METER, pose.getX(), pose.getY(), AngleUnit.RADIANS, pose.getHeading()));
    }

    public Pose2d getPose() {
        pinpoint.update();
        Pose2D oldPose = pinpoint.getPosition();
        return new Pose2d(oldPose.getX(DistanceUnit.METER), oldPose.getY(DistanceUnit.METER), oldPose.getHeading(AngleUnit.RADIANS));
    }

    @Override
    public void periodic() {
        TelemetryPacket packet = new TelemetryPacket();

        Pose2d pose = getPose();
        RobotState.getInstance().addOdometryObservation(new RobotState.OdometryObservation(RobotState.now(), pose));

        packet.put("Poses/Pinpoint/Pose/x", pose.getX()/DistanceUnit.mPerInch);
        packet.put("Poses/Pinpoint/Pose/y", pose.getY()/DistanceUnit.mPerInch);
        packet.put("Poses/Pinpoint/Pose/heading (deg)", pose.getRotation().getAngle(AngleUnit.DEGREES));

        ftcDashboard.sendTelemetryPacket(packet);
    }
}
