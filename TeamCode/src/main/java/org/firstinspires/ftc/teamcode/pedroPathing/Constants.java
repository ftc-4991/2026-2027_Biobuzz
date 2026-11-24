package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Pedro Pathing 3.0 constants. The values below are placeholders: run the AutoTune procedures in
 * {@link Tuning} (see https://pedropathing.com/docs/pathing/tuning) and paste the generated configs here.
 * If you use a different drivetrain or localizer, swap the config classes and {@link #createFollower} to match.
 */
public class Constants {
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("frontLeft");
        c.frontRightName.set("frontRight");
        c.backLeftName.set("backLeft");
        c.backRightName.set("backRight");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
    });
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-3.454089727927381);
        c.yPodOffset.set(-4.092130285548413);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.35871629108601505);
                Controller secondaryTranslationalForward = Controller.proportional(0.13253608448045387);
                Controller primaryTranslationalLateral = Controller.proportional(0.5572908221449872);
                Controller secondaryTranslationalLateral = Controller.proportional(0.205904067697552);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.01458502167213691));
                c.brake.set(Controller.proportionalFeedforward(0.012397268421316374));

                c.headingFeedback.set(Controller.proportional(9.05004355159485));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.05501745366562101, 0.007412324497137921));

                c.linearBrakeCoefficients.set(Matrix.diag(0.11813983708400248, 0.06160443428047918));
                c.quadraticBrakeCoefficients.set(Matrix.diag(8.601937382497502E-4, 0.001715418175667121));

                c.maxAchievableForwardVelocity.set(72.45862181869052);
                c.maxAchievableStrafeVelocity.set(56.91951955084433);
                c.naturalForwardDeceleration.set(36.2568210400297);
                c.naturalStrafeDeceleration.set(81.58201571440486);
            }
    );

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new Follower(
                new PinpointLocalizer(hardwareMap, localizerConfig),
                new Mecanum(hardwareMap, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }
}
