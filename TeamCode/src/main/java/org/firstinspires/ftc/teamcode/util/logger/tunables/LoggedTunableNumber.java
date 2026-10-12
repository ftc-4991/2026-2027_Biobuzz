package org.firstinspires.ftc.teamcode.util.logger.tunables;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.ValueProvider;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleSupplier;

public class LoggedTunableNumber implements DoubleSupplier, LoggedTunable<Double> {

    public static volatile boolean tuningMode = true;

    private static final Map<String, LoggedTunableNumber> registry = new LinkedHashMap<>();

    private final String key;
    private final double defaultValue;
    private volatile double value; // Dashboard writes from its websocket thread
    private final Map<Integer, Double> lastHasChangedValues = new ConcurrentHashMap<>();

    public LoggedTunableNumber(String key, double defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.value = defaultValue;

        registry.put(key, this);
        this.register();
    }

    private void register() {
        FtcDashboard dash = FtcDashboard.getInstance();
        if (dash == null) {return;}

        int slash = this.key.lastIndexOf('/');
        String category = slash < 0 ? "Tunables" : this.key.substring(0, slash);
        String name = slash < 0 ? this.key : this.key.substring(slash + 1);

        dash.removeConfigVariable(category, name);
        dash.addConfigVariable(category, name, new ValueProvider<Double>() {
            @Override public Double get() { return LoggedTunableNumber.this.value; }
            @Override public void set(Double v) { LoggedTunableNumber.this.value = v; }
        });
    }

    public static void registerAll() {
        for (LoggedTunableNumber t : registry.values()) {
            t.register();
        }
    }

    public static void logAll(TelemetryPacket packet) {
        for (LoggedTunableNumber t : registry.values()) {
            packet.put(t.key, t.get());
        }
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public Double get() {
        return tuningMode ? this.value : this.defaultValue;
    }

    @Override
    public double getAsDouble() {
        return this.get();
    }

    public void set(double newValue) {
        this.value = newValue;
    }

    @Override
    public boolean hasChanged(int id) {
        double current = this.get();
        Double last = this.lastHasChangedValues.get(id);
        if (last == null || current != last) {
            this.lastHasChangedValues.put(id, current);
            return true;
        }
        return false;
    }
}