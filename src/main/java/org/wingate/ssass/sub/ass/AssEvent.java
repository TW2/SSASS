package org.wingate.ssass.sub.ass;

import org.wingate.ssass.sub.common.Time;
import org.wingate.ssass.sub.common.Unit;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssEvent {
    private boolean visible;
    private int layer;
    private Time start;
    private Time end;
    private AssStyle style;
    private String name;
    private double marginL;
    private double marginR;
    private double marginV;
    private String effect;
    private String text;

    public AssEvent(boolean visible, int layer, Time start, Time end,
                    AssStyle style, String name,
                    double marginL, double marginR, double marginV,
                    String effect, String text) {
        this.visible = visible;
        this.layer = layer;
        this.start = start;
        this.end = end;
        this.style = style;
        this.name = name;
        this.marginL = marginL;
        this.marginR = marginR;
        this.marginV = marginV;
        this.effect = effect;
        this.text = text;
    }

    public AssEvent() {
        this(true, 0, new Time(0d), new Time(5_000_000d),
                new AssStyle("Arial", 14f, Color.white),
                "", 0d, 0d, 0d, "",
                "Juliette is the name of Bob's kitten!");
    }

    public static Time toTime(String rawTime){
        Time t = new Time();
        Pattern p = Pattern.compile("(\\d+):(\\d+):(\\d+).(\\d+)");
        Matcher m = p.matcher(rawTime);
        if(m.find()){
            int hh = Integer.parseInt(m.group(1));
            int mm = Integer.parseInt(m.group(2));
            int ss = Integer.parseInt(m.group(3));
            int cc = Integer.parseInt(m.group(4));
            long ms = hh* 3600000L
                    + mm* 60000L
                    + ss* 1000L
                    + cc* 10L;
            t = Time.from(ms, Unit.Millisecond);
        }
        return t;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    public AssStyle getStyle() {
        return style;
    }

    public void setStyle(AssStyle style) {
        this.style = style;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMarginL() {
        return marginL;
    }

    public void setMarginL(double marginL) {
        this.marginL = marginL;
    }

    public double getMarginR() {
        return marginR;
    }

    public void setMarginR(double marginR) {
        this.marginR = marginR;
    }

    public double getMarginV() {
        return marginV;
    }

    public void setMarginV(double marginV) {
        this.marginV = marginV;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
