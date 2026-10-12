package org.firstinspires.ftc.teamcode.util.logger.tunables;

public interface Tunable<T> {
    LoggedTunable<T> makeTunable(String key);
}
