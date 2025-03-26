package org.wingate.ssass.sub.common;

public class Time {
    private double micros;

    public Time(double micros) {
        this.micros = micros;
    }

    public Time() {
        this(0L);
    }

    public static Time from(double value, Unit fromUnit){
        switch(fromUnit){
            case Hour -> { return new Time(value * 3_600_000_000d); }
            case Minute -> { return new Time(value * 60_000_000d); }
            case Second -> { return new Time(value * 1_000_000d); }
            case Millisecond -> { return new Time(value * 1_000d); }
            case Microsecond -> { return new Time(value); }
            case Nanosecond -> { return new Time(value / 1_000d); }
            default -> { return new Time(); }
        }
    }

    public static double to(Time t, Unit toUnit){
        switch(toUnit){
            case Hour -> { return t.micros / 3_600_000_000d; }
            case Minute -> { return t.micros / 60_000_000d; }
            case Second -> { return t.micros / 1_000_000d; }
            case Millisecond -> { return t.micros / 1_000d; }
            case Microsecond -> { return t.micros; }
            case Nanosecond -> { return t.micros * 1_000d; }
            default -> { return 0d; }
        }
    }

    public double getMicros() {
        return micros;
    }

    public void setMicros(double micros) {
        this.micros = micros;
    }
}
