package org.firstinspires.ftc.teamcode.robot;

import com.acmerobotics.dashboard.config.Config;
import com.seattlesolvers.solverslib.geometry.Pose2d;
import com.seattlesolvers.solverslib.geometry.Rotation2d;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Config
public class RobotState {
    private static RobotState instance;
    public static RobotState getInstance() {
        if (instance == null) {instance = new RobotState();}
        return instance;
    }

    public static double ODOM_STD_X = 0.003, ODOM_STD_Y = 0.003, ODOM_STD_THETA = 0.002;
    public static double POSE_BUFFER_SIZE_SECS = 2.0;
    public static double TXTY_STALE_TIME_SECS = 0.2;

    private Pose2d odometryPose = null;
    private Pose2d estimatedGlobalPose = new Pose2d();

    private final TreeMap<Double, Pose2d> poseBuffer = new TreeMap<>();
    private final Map<Integer, TxTyObservation> txtyObservations = new HashMap<>();

    private RobotState() {

    }

    public static double now() {
        return System.nanoTime() * 1e-9;
    }

    public void reset() {
        odometryPose = null;
        estimatedGlobalPose = new Pose2d();
        poseBuffer.clear();
        txtyObservations.clear();
    }

    public void log(Telemetry telemetry) {
        if (odometryPose != null) {
            telemetry.addData("Odometry Pose", fmt(odometryPose));
        }
        telemetry.addData("Estimated Global Pose", fmt(estimatedGlobalPose));
        for (TxTyObservation o : txtyObservations.values()) {
            telemetry.addData("Tag " + o.tagID, fmt(o.pose) + " @ " + o.timestamp);
        }
    }

    private static String fmt(Pose2d p) {
        return String.format("(%.2f, %.2f, %.1f deg)", p.getX(), p.getY(), Math.toDegrees(p.getHeading()));
    }

    public Pose2d getEstimatedGlobalPose() {
        return estimatedGlobalPose;
    }

    public void resetPose(Pose2d pose) {
        estimatedGlobalPose = pose;
        poseBuffer.clear();
    }

    public void addOdometryObservation(OdometryObservation observation) {
        Pose2d last = odometryPose;
        odometryPose = observation.pose;
        addSample(observation.timestamp, odometryPose);

        // First sample just seeds the odometry pose
        if (last == null) {return;}

        double[] twist = log(last, odometryPose);
        estimatedGlobalPose = exp(estimatedGlobalPose, twist);
    }

    public void addVisionObservation(VisionObservation observation) {
        if (odometryPose == null || poseBuffer.isEmpty()) {return;}
        if (poseBuffer.lastKey() - POSE_BUFFER_SIZE_SECS > observation.timestamp) {return;}

        Pose2d sample = getSample(observation.timestamp);
        if (sample == null) {return;}

        double[] sampleToOdometry = relative(sample, odometryPose);
        double[] odometryToSample = relative(odometryPose, sample);

        Pose2d globalEstimateAtTime = apply(estimatedGlobalPose, odometryToSample);

        double[] error = relative(globalEstimateAtTime, observation.pose);
        double[] scaled = new double[3];
        double[] odomStd = {ODOM_STD_X, ODOM_STD_Y, ODOM_STD_THETA};
        for (int i = 0; i < 3; i++) {
            double q = odomStd[i] * odomStd[i];
            double r = observation.stdDevs[i] * observation.stdDevs[i];
            double k = (q == 0.0) ? 0.0 : q / (q + Math.sqrt(q * r));
            scaled[i] = k * error[i];
        }

        estimatedGlobalPose = apply(apply(globalEstimateAtTime, scaled), sampleToOdometry);
    }

    public void addTxTyObservation(TxTyObservation observation) {
        TxTyObservation previous = txtyObservations.get(observation.tagID);
        if (previous == null || observation.timestamp > previous.timestamp) {
            txtyObservations.put(observation.tagID, observation);
        }
    }

    public Optional<Pose2d> getRobotPoseFromTag(int tagID) {
        TxTyObservation observation = txtyObservations.get(tagID);
        if (observation == null || odometryPose == null) {return Optional.empty();}
        if (now() - observation.timestamp >= TXTY_STALE_TIME_SECS) {return Optional.empty();}

        Pose2d sample = getSample(observation.timestamp);
        if (sample == null) {return Optional.empty();}

        return Optional.of(apply(observation.pose, relative(sample, odometryPose)));
    }

    public boolean isTagStale(int tagID) {
        TxTyObservation observation = txtyObservations.get(tagID);
        if (observation == null) {return true;}
        return now() - observation.timestamp >= TXTY_STALE_TIME_SECS;
    }

    private void addSample(double timestamp, Pose2d pose) {
        poseBuffer.put(timestamp, pose);
        while (!poseBuffer.isEmpty() && poseBuffer.lastKey() - poseBuffer.firstKey() > POSE_BUFFER_SIZE_SECS) {
            poseBuffer.pollFirstEntry();
        }
    }

    private Pose2d getSample(double timestamp) {
        if (poseBuffer.isEmpty()) {return null;}

        Map.Entry<Double, Pose2d> floor = poseBuffer.floorEntry(timestamp);
        Map.Entry<Double, Pose2d> ceil = poseBuffer.ceilingEntry(timestamp);

        if (floor == null) {return ceil.getValue();}
        if (ceil == null || floor.getKey().equals(ceil.getKey())) {return floor.getValue();}

        double t = (timestamp - floor.getKey()) / (ceil.getKey() - floor.getKey());
        Pose2d a = floor.getValue();
        Pose2d b = ceil.getValue();
        double heading = a.getHeading() + t * wrap(b.getHeading() - a.getHeading());
        return new Pose2d(
                a.getX() + t * (b.getX() - a.getX()),
                a.getY() + t * (b.getY() - a.getY()),
                new Rotation2d(heading)
        );
    }

    private static double wrap(double radians) {
        return Math.atan2(Math.sin(radians), Math.cos(radians));
    }

    private static double[] relative(Pose2d a, Pose2d b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double cos = Math.cos(a.getHeading());
        double sin = Math.sin(a.getHeading());
        return new double[] {
                dx * cos + dy * sin,
                -dx * sin + dy * cos,
                wrap(b.getHeading() - a.getHeading())
        };
    }

     private static Pose2d apply(Pose2d pose, double[] t) {
        double cos = Math.cos(pose.getHeading());
        double sin = Math.sin(pose.getHeading());
        return new Pose2d(
                pose.getX() + t[0] * cos - t[1] * sin,
                pose.getY() + t[0] * sin + t[1] * cos,
                new Rotation2d(pose.getHeading() + t[2])
        );
    }

    private static double[] log(Pose2d a, Pose2d b) {
        double[] rel = relative(a, b);
        double dtheta = rel[2];
        double halfDtheta = dtheta / 2.0;
        double cosMinusOne = Math.cos(dtheta) - 1.0;

        double halfThetaByTanHalf;
        if (Math.abs(cosMinusOne) < 1e-9) {
            halfThetaByTanHalf = 1.0 - dtheta * dtheta / 12.0;
        } else {
            halfThetaByTanHalf = -(halfDtheta * Math.sin(dtheta)) / cosMinusOne;
        }

        double mag = Math.hypot(halfThetaByTanHalf, halfDtheta);
        double ang = Math.atan2(-halfDtheta, halfThetaByTanHalf);
        double tx = rel[0] * Math.cos(ang) - rel[1] * Math.sin(ang);
        double ty = rel[0] * Math.sin(ang) + rel[1] * Math.cos(ang);
        return new double[] {tx * mag, ty * mag, dtheta};
    }

    private static Pose2d exp(Pose2d pose, double[] twist) {
        double dtheta = twist[2];
        double sinTheta = Math.sin(dtheta);
        double cosTheta = Math.cos(dtheta);

        double s;
        double c;
        if (Math.abs(dtheta) < 1e-9) {
            s = 1.0 - dtheta * dtheta / 6.0;
            c = 0.5 * dtheta;
        } else {
            s = sinTheta / dtheta;
            c = (1.0 - cosTheta) / dtheta;
        }

        double[] transform = {
                twist[0] * s - twist[1] * c,
                twist[0] * c + twist[1] * s,
                dtheta
        };
        return apply(pose, transform);
    }

    public static final class OdometryObservation {
        public final double timestamp;
        public final Pose2d pose;

        public OdometryObservation(double timestamp, Pose2d pose) {
            this.timestamp = timestamp;
            this.pose = pose;
        }
    }

    public static final class VisionObservation {
        public final double timestamp;
        public final Pose2d pose;
        public final double[] stdDevs;

        public VisionObservation(double timestamp, Pose2d pose, double[] stdDevs) {
            this.timestamp = timestamp;
            this.pose = pose;
            this.stdDevs = stdDevs;
        }
    }

    public static final class TxTyObservation {
        public final double timestamp;
        public final int tagID;
        public final Pose2d pose;

        public TxTyObservation(double timestamp, int tagID, Pose2d pose) {
            this.timestamp = timestamp;
            this.tagID = tagID;
            this.pose = pose;
        }
    }
}