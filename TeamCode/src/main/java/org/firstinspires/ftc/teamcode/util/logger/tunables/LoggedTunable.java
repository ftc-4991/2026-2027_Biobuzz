package org.firstinspires.ftc.teamcode.util.logger.tunables;

import com.pedropathing.math.Pose;
import com.pedropathing.utils.Angle;
import com.seattlesolvers.solverslib.geometry.Pose2d;
import com.seattlesolvers.solverslib.geometry.Rotation2d;
import com.seattlesolvers.solverslib.geometry.Translation2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.function.DoubleFunction;
import java.util.function.Function;

public interface LoggedTunable<T> {

    T get();

    boolean hasChanged(int id);

    static boolean hasChanged(int id, LoggedTunable<?>... tunables) {
        if (!LoggedTunableNumber.tuningMode) {
            return false;
        }
        boolean out = false;
        for (LoggedTunable<?> tunable : tunables) {
            if (tunable.hasChanged(id)) {
                out = true;
            }
        }
        return out;
    }

    static LoggedTunableNumber from(String key, double defaultValue) {
        return new LoggedTunableNumber(key, defaultValue);
    }

    static <T, U extends Tunable<T>> LoggedTunable<T> from(String key, U defaultValue) {
        return defaultValue.makeTunable(key);
    }

    static <T> LoggedTunable<T> from(final String key, final DoubleFunction<T> constructor, double defaultValue) {
        return new LoggedTunable<T>() {
            private final LoggedTunableNumber tunableNumber = LoggedTunable.from(key, defaultValue);
            private T cache = constructor.apply(defaultValue);

            @Override
            public boolean hasChanged(int id) {
                return LoggedTunable.hasChanged(id, this.tunableNumber);
            }

            @Override
            public T get() {
                if (this.hasChanged(this.hashCode())) {
                    this.cache = constructor.apply(this.tunableNumber.getAsDouble());
                }
                return this.cache;
            }
        };
    }

    default <U> LoggedTunable<U> map(final Function<T, U> mappingFunction) {
        final LoggedTunable<T> parent = this;
        return new LoggedTunable<U>() {
            private U cache = mappingFunction.apply(parent.get());

            @Override
            public boolean hasChanged(int id) {
                return parent.hasChanged(id);
            }

            @Override
            public U get() {
                if (this.hasChanged(this.hashCode())) {
                    this.cache = mappingFunction.apply(parent.get());
                }
                return this.cache;
            }
        };
    }

    public static LoggedTunable<Translation2d> from(String key, Translation2d defaultValue) {
        return new LoggedTunable<Translation2d>() {
            private final LoggedTunableNumber x = LoggedTunable.from(key + "/x", defaultValue.getX());
            private final LoggedTunableNumber y = LoggedTunable.from(key + "/y", defaultValue.getY());

            private Translation2d cache = defaultValue;

            @Override
            public boolean hasChanged(int id) {
                return LoggedTunable.hasChanged(id, this.x, this.y);
            }

            @Override
            public Translation2d get() {
                if (this.hasChanged(this.hashCode())) {
                    this.cache = new Translation2d(this.x.get(), this.y.get());
                }
                return this.cache;
            }
        };
    }

    public static LoggedTunable<Translation2d> from(String key, DistanceUnit dashboardUnit, DistanceUnit valueUnit, Translation2d defaultValue) {
        return new LoggedTunable<Translation2d>() {
            // Dashboard side: stored in dashboardUnit. get() converts back to valueUnit.
            private final LoggedTunable<Double> x = LoggedTunable.from(
                    key + "/x",
                    (double v) -> valueUnit.fromUnit(dashboardUnit, v),
                    dashboardUnit.fromUnit(valueUnit, defaultValue.getX())
            );
            private final LoggedTunable<Double> y = LoggedTunable.from(
                    key + "/y",
                    (double v) -> valueUnit.fromUnit(dashboardUnit, v),
                    dashboardUnit.fromUnit(valueUnit, defaultValue.getY())
            );

            private Translation2d cache = defaultValue;

            @Override
            public boolean hasChanged(int id) {
                return LoggedTunable.hasChanged(id, this.x, this.y);
            }

            @Override
            public Translation2d get() {
                if (this.hasChanged(this.hashCode())) {
                    this.cache = new Translation2d(this.x.get(), this.y.get());
                }
                return this.cache;
            }
        };
    }

    public static LoggedTunable<Rotation2d> from(String key, Rotation2d defaultValue) {
        return new LoggedTunable<Rotation2d>() {
            private final LoggedTunableNumber theta = LoggedTunable.from(key + "/theta", defaultValue.getRadians());

            private Rotation2d cache = defaultValue;

            @Override
            public boolean hasChanged(int id) {
                return LoggedTunable.hasChanged(id, this.theta);
            }

            @Override
            public Rotation2d get() {
                if (this.hasChanged(this.hashCode())) {
                    this.cache = new Rotation2d(this.theta.get());
                }
                return this.cache;
            }
        };
    }

    public static LoggedTunable<Rotation2d> from(String key, AngleUnit dashboardUnit, AngleUnit valueUnit, Rotation2d defaultValue) {
        return new LoggedTunable<Rotation2d>() {
            private final LoggedTunable<Double> theta = LoggedTunable.from(key + "/theta", (double v) -> valueUnit.fromUnit(dashboardUnit, v), dashboardUnit.fromUnit(valueUnit, defaultValue.getAngle(valueUnit)));

            private Rotation2d cache = defaultValue;

            @Override
            public Rotation2d get() {
                if (this.hasChanged(this.hashCode())) {
                    this.cache = new Rotation2d(this.theta.get());
                }
                return this.cache;
            }

            @Override
            public boolean hasChanged(int id) {
                return LoggedTunable.hasChanged(id, this.theta);
            }
        };
    }

    public static LoggedTunable<Pose2d> from(String key, Pose2d defaultValue) {
        return new LoggedTunable<Pose2d>() {
            private final LoggedTunable<Translation2d> translation = LoggedTunable.from(key + "/translation", defaultValue.getTranslation());
            private final LoggedTunable<Rotation2d> rotation = LoggedTunable.from(key + "/rotation", defaultValue.getRotation());

            private Pose2d cache = defaultValue;

            @Override
            public boolean hasChanged(int id) {
                return LoggedTunable.hasChanged(id, this.translation, this.rotation);
            }

            @Override
            public Pose2d get() {
                if (this.hasChanged(this.hashCode())) {
                    this.cache = new Pose2d(this.translation.get(), this.rotation.get());
                }
                return this.cache;
            }
        };
    }

    public static LoggedTunable<Pose2d> from(String key, DistanceUnit translationDashboardUnit, DistanceUnit translationValueUnit, AngleUnit rotationDashboardUnit, AngleUnit rotationValueUnit, Pose2d defaultValue) {
        return new LoggedTunable<Pose2d>() {
            private final LoggedTunable<Translation2d> translation = LoggedTunable.from(key + "/translation", translationDashboardUnit, translationValueUnit, defaultValue.getTranslation());
            private final LoggedTunable<Rotation2d> rotation = LoggedTunable.from(key + "/rotation", rotationDashboardUnit, rotationValueUnit, defaultValue.getRotation());

            private Pose2d cache = defaultValue;

            @Override
            public Pose2d get() {
                if (this.hasChanged(this.hashCode())) {
                    this.cache = new Pose2d(this.translation.get(), this.rotation.get());
                }
                return this.cache;
            }

            @Override
            public boolean hasChanged(int id) {
                return LoggedTunable.hasChanged(id, this.translation, this.rotation);
            }
        };
    }
}