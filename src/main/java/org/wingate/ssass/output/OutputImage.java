package org.wingate.ssass.output;

import org.wingate.ssass.ass.ASS;
import org.wingate.ssass.ass.AssEvent;
import org.wingate.ssass.ass.AssStyle;
import org.wingate.ssass.ass.AssTime;
import org.wingate.ssass.render.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class OutputImage {

    public static Map<Integer, BufferedImage> getImages(
            ASS ass, long micros, int width, int height) {

        final List<AssEvent> events = new ArrayList<>();

        for (AssEvent event : ass.getEvents()) {
            long start = Math.round(event.getStart().getMsTime() * 1000d);
            long end = Math.round(event.getEnd().getMsTime() * 1000d);
            if (start <= micros && micros < end) {
                events.add(event);
            }
        }

        Map<Integer, BufferedImage> images = new HashMap<>();

        for(AssEvent event : events){
            images.put(event.getLayer(), doImage(ass, event, micros, width, height));
        }

        return images;
    }

    /**
     * Render one event only
     * @param ass your loaded ASS data
     * @param event the event you want to render
     * @param width the width of the resulting image
     * @param height the height of the resulting image
     * @return an image
     */
    public static BufferedImage getImage(
            ASS ass, AssEvent event, AssTime time, int width, int height) {
        long micros = Math.round(time.getMsTime() * 1000d);
        return doImage(ass, event, micros, width, height);
    }

    private static BufferedImage doImage(ASS ass, AssEvent event, long micros, int width, int height){
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
        );

        Converter converter = Converter.createShapes(event, ass);
        // Calculation
        double sentenceWidth = 0d;
        double sentenceHeight = 0d;
        for(AGraphicElement element : converter.getElements()){
            if(element instanceof Char c){
                sentenceWidth += c.getAdvance();
                sentenceWidth += c.getExtraSpacing();
                sentenceHeight = Math.max(sentenceHeight, c.getHeight());
            }
        }

        // Apply position (take into account movement)
        AffineTransform tr = doPosition(event, micros, width, height,
                sentenceWidth, sentenceHeight, converter);
        g.setTransform(tr);

        // Draw
        for(AGraphicElement element : converter.getElements()){
            if(element instanceof Char c){
                // TODO Shadow
                g.setColor(doShadowColor(event, micros, converter)); // 4c 4a alpha fad fade
                // Outline
                g.setColor(doOutlineColor(event, micros, converter)); // 3c 3a alpha fad fade
                c.draw(g);
                // Text
                g.setColor(doTextColor(event, micros, converter)); // c 1c 1a alpha fad fade
                c.fill(g);
                tr.translate(c.getAdvance() + c.getExtraSpacing(), 0d);
                g.setTransform(tr);
                // TODO Karaoke
                g.setColor(doKaraokeColor(event, micros, converter)); // 2c 2a alpha fad fade
            }
        }

        g.dispose();
        return image;
    }

    private static AffineTransform doPosition(AssEvent event, long micros,
               int width, int height, double sentenceWidth, double sentenceHeight,
               Converter converter){

        // Style
        AssStyle style = event.getStyle();

        // Margins
        double ml = event.getMarginL() > 0 ? event.getMarginL() : style.getMarginL();
        double mr = event.getMarginR() > 0 ? event.getMarginR() : style.getMarginR();
        double mv = event.getMarginV() > 0 ? event.getMarginV() : style.getMarginV();

        // Available distance
        double visibleWidth = width - ml - mr;
        double visibleHeight = height - (2 * mv);

        // Margins offset :
        // left = ml
        // right = width - mr
        // top = mv
        // bottom = height - mv

        // Coordinates to put the first char
        double x = 0d;
        double y = 0d;

        // Search in overrides a or an
        switch(converter.getElements().getFirst()){
            case Char v -> {
                for(Map<Tag, String> map : v.getTags()){
                    for(Map.Entry<Tag, String> entry : map.entrySet()){
                        String s = entry.getValue();
                        switch(entry.getKey()){
                            case Tag.AlignmentLegacy -> {
                                if(!s.isEmpty()){
                                    switch(Integer.parseInt(s)){
                                        case 1 -> {
                                            x = ml;
                                            y = mv + visibleHeight;
                                        }
                                        case 2 -> {
                                            x = ml + (visibleWidth / 2d) - (sentenceWidth / 2d);
                                            y = mv + visibleHeight;
                                        }
                                        case 3 -> {
                                            x = (ml + visibleWidth - sentenceWidth);
                                            y = mv + visibleHeight;
                                        }
                                        case 9 -> { // an eq 4
                                            x = ml;
                                            y = mv + (visibleHeight / 2d) + (sentenceHeight / 2d);
                                        }
                                        case 10 -> { // an eq 5
                                            x = ml + (visibleWidth / 2d) - (sentenceWidth / 2d);
                                            y = mv + (visibleHeight / 2d) + (sentenceHeight / 2d);
                                        }
                                        case 11 -> { // an eq 6
                                            x = (ml + visibleWidth - sentenceWidth);
                                            y = mv + (visibleHeight / 2d) + (sentenceHeight / 2d);
                                        }
                                        case 5 -> { // an eq 7
                                            x = ml;
                                            y = mv + sentenceHeight;
                                        }
                                        case 6 -> { // an eq 8
                                            x = ml + (visibleWidth / 2d) - (sentenceWidth / 2d);
                                            y = mv + sentenceHeight;
                                        }
                                        case 7 -> { // an eq 9
                                            x = (ml + visibleWidth - sentenceWidth);
                                            y = mv + sentenceHeight;
                                        }
                                    }
                                }
                            }
                            case Tag.AlignmentNumPad -> {
                                if(!s.isEmpty()){
                                    switch(Integer.parseInt(s)){
                                        case 1 -> {
                                            x = ml;
                                            y = mv + visibleHeight;
                                        }
                                        case 2 -> {
                                            x = ml + (visibleWidth / 2d) - (sentenceWidth / 2d);
                                            y = mv + visibleHeight;
                                        }
                                        case 3 -> {
                                            x = (ml + visibleWidth - sentenceWidth);
                                            y = mv + visibleHeight;
                                        }
                                        case 4 -> {
                                            x = ml;
                                            y = mv + (visibleHeight / 2d) + (sentenceHeight / 2d);
                                        }
                                        case 5 -> {
                                            x = ml + (visibleWidth / 2d) - (sentenceWidth / 2d);
                                            y = mv + (visibleHeight / 2d) + (sentenceHeight / 2d);
                                        }
                                        case 6 -> {
                                            x = (ml + visibleWidth - sentenceWidth);
                                            y = mv + (visibleHeight / 2d) + (sentenceHeight / 2d);
                                        }
                                        case 7 -> {
                                            x = ml;
                                            y = mv + sentenceHeight;
                                        }
                                        case 8 -> {
                                            x = ml + (visibleWidth / 2d) - (sentenceWidth / 2d);
                                            y = mv + sentenceHeight;
                                        }
                                        case 9 -> {
                                            x = (ml + visibleWidth - sentenceWidth);
                                            y = mv + sentenceHeight;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            case Drawing v -> {

            }
            default -> {}
        }

        // Position or movement
        double xp = 0d;
        double yp = 0d;

        // Add pos or move
        switch(converter.getElements().getFirst()){
            case Char v -> {
                for(Map<Tag, String> map : v.getTags()){
                    for(Map.Entry<Tag, String> entry : map.entrySet()){
                        switch(entry.getKey()){
                            case Tag.Position -> {
                                if(Tag.valuesOf(entry) instanceof String[] value){
                                    if(value.length == entry.getKey().getMaxParameters()){
                                        xp = Double.parseDouble(value[0]);
                                        yp = Double.parseDouble(value[1]);
                                    }
                                }
                            }
                            case Tag.Movement -> {
                                if(Tag.valuesOf(entry) instanceof String[] value){
                                    if(value.length == entry.getKey().getMaxParameters()){
                                        // move x1 y1 x2 y2 t1 t2
                                        double x1 = Double.parseDouble(value[0]); // 0%
                                        double y1 = Double.parseDouble(value[1]); // 0%
                                        double x2 = Double.parseDouble(value[2]); // 100%
                                        double y2 = Double.parseDouble(value[3]); // 100%
                                        long s = Math.round(Double.parseDouble(value[4])) * 1000; // 0%
                                        long e = Math.round(Double.parseDouble(value[5])) * 1000; // 100%
                                        double xt = x2 - x1; // 100%
                                        double yt = y2 - y1; // 100%
                                        long t = Math.max(s, e) - Math.min(s, e); // 100%
                                        // micros <> xp and yp
                                        // t <> xt and yt
                                        xp = xt * micros / Math.max(1, t);
                                        yp = yt * micros / Math.max(1, t);
                                    }else if(value.length == entry.getKey().getMaxParameters() - 2){
                                        // move x1 y1 x2 y2 (total time of sentence)
                                        double x1 = Double.parseDouble(value[0]); // 0%
                                        double y1 = Double.parseDouble(value[1]); // 0%
                                        double x2 = Double.parseDouble(value[2]); // 100%
                                        double y2 = Double.parseDouble(value[3]); // 100%
                                        long s = (long) (event.getStart().getMsTime() * 1000d); // 0%
                                        long e = (long) (event.getEnd().getMsTime() * 1000d); // 100%
                                        double xt = x2 - x1; // 100%
                                        double yt = y2 - y1; // 100%
                                        long t = Math.max(s, e) - Math.min(s, e); // 100%
                                        // micros <> xp and yp
                                        // t <> xt and yt
                                        xp = xt * micros / Math.max(1, t);
                                        yp = yt * micros / Math.max(1, t);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            case Drawing v -> {

            }
            default -> {}
        }

        AffineTransform tr = new AffineTransform();
        tr.translate(x + xp, y + yp);

        return tr;
    }

    private static Color doTextColor(AssEvent event, long micros, Converter converter){
        Color c = event.getStyle().getTextColor().getColor();

        int trans = event.getStyle().getTextColor().getColor().getTransparency();

        switch(converter.getElements().getFirst()){
            case Char v -> {
                // Alpha
                for(Map<Tag, String> map : v.getTags()){
                    for(Map.Entry<Tag, String> entry : map.entrySet()){
                        switch(entry.getKey()){
                            case Tag.TextAlpha, Tag.Alpha -> {
                                if(Tag.valuesOf(entry) instanceof Integer value){
                                    trans = value;
                                }
                            }
                        }
                    }
                }

                for(Map<Tag, String> map : v.getTags()){
                    for(Map.Entry<Tag, String> entry : map.entrySet()){
                        switch(entry.getKey()){
                            case Tag.FadeSimple -> {
                                if(Tag.valuesOf(entry) instanceof String[] value){
                                    // Fade in
                                    long s1 = (long) (event.getStart().getMsTime() * 1000);
                                    long t1 = Long.parseLong(value[0]) * 1000;
                                    long s2 = t1 + s1;
                                    // t <> 255
                                    // micros <> x
                                    if(s1 <= micros && micros <= s2 && t1 > 0){
                                        trans = Math.toIntExact(255 * micros / t1);
                                    }

                                    // Fade out
                                    long e4 = (long) (event.getEnd().getMsTime() * 1000);
                                    long t2 = Long.parseLong(value[1]) * 1000;
                                    long e3 = e4 - t2;
                                    // t <> 255
                                    // micros <> x
                                    if(e3 <= micros && micros <= e4 && t2 > 0){
                                        trans = Math.toIntExact(255 * micros / t2);
                                    }
                                }
                            }
                            case Tag.FadeComplex -> {
                                if(Tag.valuesOf(entry) instanceof String[] value){
                                    int a1 = Integer.parseInt(value[0]);
                                    int a2 = Integer.parseInt(value[1]);
                                    int a3 = Integer.parseInt(value[2]);
                                    long t1 = Long.parseLong(value[3]);
                                    long t2 = Long.parseLong(value[4]);
                                    long t3 = Long.parseLong(value[5]);
                                    long t4 = Long.parseLong(value[6]);
                                    long p1 = t2 - t1;
                                    long p2 = t4 - t3;
                                    // Fade in (t <> 255 ; micros <> x)
                                    if(t1 <= micros && micros <= t2 && p1 > 0){
                                        int p = a2 - a1;
                                        trans = Math.toIntExact((p * micros / p1) + a1);
                                    }
                                    // Fade out (t <> 255 ; micros <> x)
                                    if(t3 <= micros && micros <= t4 && p2 > 0){
                                        int p = a3 - a2;
                                        trans = Math.toIntExact((p * micros / p2) + a2);
                                    }
                                }
                            }
                        }
                    }
                }

                // Color
                for(Map<Tag, String> map : v.getTags()){
                    for(Map.Entry<Tag, String> entry : map.entrySet()){
                        switch(entry.getKey()){
                            case Tag.TextColor, Tag.PrimaryColor -> {
                                if(Tag.valuesOf(entry) instanceof Color value){
                                    c = value;
                                }
                            }
                        }
                    }
                }
            }
            case Drawing v -> {

            }
            default -> {}
        }

        return new Color(c.getRed(), c.getGreen(), c.getBlue(), trans);
    }

    private static Color doKaraokeColor(AssEvent event, long micros, Converter converter){
        Color c = event.getStyle().getTextColor().getColor();

        int trans = event.getStyle().getTextColor().getColor().getTransparency();

        switch(converter.getElements().getFirst()){
            case Char v -> {
                // Alpha
                for(Map<Tag, String> map : v.getTags()){
                    for(Map.Entry<Tag, String> entry : map.entrySet()){
                        switch(entry.getKey()){
                            case Tag.KaraokeAlpha, Tag.Alpha -> {
                                if(Tag.valuesOf(entry) instanceof Integer value){
                                    trans = value;
                                }
                            }
                        }
                    }
                }

                for(Map<Tag, String> map : v.getTags()){
                    for(Map.Entry<Tag, String> entry : map.entrySet()){
                        switch(entry.getKey()){
                            case Tag.FadeSimple -> {
                                if(Tag.valuesOf(entry) instanceof String[] value){
                                    // Fade in
                                    long s1 = (long) (event.getStart().getMsTime() * 1000);
                                    long t1 = Long.parseLong(value[0]) * 1000;
                                    long s2 = t1 + s1;
                                    // t <> 255
                                    // micros <> x
                                    if(s1 <= micros && micros <= s2 && t1 > 0){
                                        trans = Math.toIntExact(255 * micros / t1);
                                    }

                                    // Fade out
                                    long e4 = (long) (event.getEnd().getMsTime() * 1000);
                                    long t2 = Long.parseLong(value[1]) * 1000;
                                    long e3 = e4 - t2;
                                    // t <> 255
                                    // micros <> x
                                    if(e3 <= micros && micros <= e4 && t2 > 0){
                                        trans = Math.toIntExact(255 * micros / t2);
                                    }
                                }
                            }
                            case Tag.FadeComplex -> {
                                if(Tag.valuesOf(entry) instanceof String[] value){
                                    int a1 = Integer.parseInt(value[0]);
                                    int a2 = Integer.parseInt(value[1]);
                                    int a3 = Integer.parseInt(value[2]);
                                    long t1 = Long.parseLong(value[3]);
                                    long t2 = Long.parseLong(value[4]);
                                    long t3 = Long.parseLong(value[5]);
                                    long t4 = Long.parseLong(value[6]);
                                    long p1 = t2 - t1;
                                    long p2 = t4 - t3;
                                    // Fade in (t <> 255 ; micros <> x)
                                    if(t1 <= micros && micros <= t2 && p1 > 0){
                                        int p = a2 - a1;
                                        trans = Math.toIntExact((p * micros / p1) + a1);
                                    }
                                    // Fade out (t <> 255 ; micros <> x)
                                    if(t3 <= micros && micros <= t4 && p2 > 0){
                                        int p = a3 - a2;
                                        trans = Math.toIntExact((p * micros / p2) + a2);
                                    }
                                }
                            }
                        }
                    }
                }

                // Color
                for(Map<Tag, String> map : v.getTags()){
                    for(Map.Entry<Tag, String> entry : map.entrySet()){
                        if (Objects.requireNonNull(entry.getKey()) == Tag.KaraokeColor) {
                            if (Tag.valuesOf(entry) instanceof Color value) {
                                c = value;
                            }
                        }
                    }
                }
            }
            case Drawing v -> {

            }
            default -> {}
        }

        return new Color(c.getRed(), c.getGreen(), c.getBlue(), trans);
    }

    private static Color doOutlineColor(AssEvent event, long micros, Converter converter){
        Color c = event.getStyle().getTextColor().getColor();

        int trans = event.getStyle().getTextColor().getColor().getTransparency();

        switch(converter.getElements().getFirst()){
            case Char v -> {
                // Alpha
                for(Map<Tag, String> map : v.getTags()){
                    for(Map.Entry<Tag, String> entry : map.entrySet()){
                        switch(entry.getKey()){
                            case Tag.OutlineAlpha, Tag.Alpha -> {
                                if(Tag.valuesOf(entry) instanceof Integer value){
                                    trans = value;
                                }
                            }
                        }
                    }
                }

                for(Map<Tag, String> map : v.getTags()){
                    for(Map.Entry<Tag, String> entry : map.entrySet()){
                        switch(entry.getKey()){
                            case Tag.FadeSimple -> {
                                if(Tag.valuesOf(entry) instanceof String[] value){
                                    // Fade in
                                    long s1 = (long) (event.getStart().getMsTime() * 1000);
                                    long t1 = Long.parseLong(value[0]) * 1000;
                                    long s2 = t1 + s1;
                                    // t <> 255
                                    // micros <> x
                                    if(s1 <= micros && micros <= s2 && t1 > 0){
                                        trans = Math.toIntExact(255 * micros / t1);
                                    }

                                    // Fade out
                                    long e4 = (long) (event.getEnd().getMsTime() * 1000);
                                    long t2 = Long.parseLong(value[1]) * 1000;
                                    long e3 = e4 - t2;
                                    // t <> 255
                                    // micros <> x
                                    if(e3 <= micros && micros <= e4 && t2 > 0){
                                        trans = Math.toIntExact(255 * micros / t2);
                                    }
                                }
                            }
                            case Tag.FadeComplex -> {
                                if(Tag.valuesOf(entry) instanceof String[] value){
                                    int a1 = Integer.parseInt(value[0]);
                                    int a2 = Integer.parseInt(value[1]);
                                    int a3 = Integer.parseInt(value[2]);
                                    long t1 = Long.parseLong(value[3]);
                                    long t2 = Long.parseLong(value[4]);
                                    long t3 = Long.parseLong(value[5]);
                                    long t4 = Long.parseLong(value[6]);
                                    long p1 = t2 - t1;
                                    long p2 = t4 - t3;
                                    // Fade in (t <> 255 ; micros <> x)
                                    if(t1 <= micros && micros <= t2 && p1 > 0){
                                        int p = a2 - a1;
                                        trans = Math.toIntExact((p * micros / p1) + a1);
                                    }
                                    // Fade out (t <> 255 ; micros <> x)
                                    if(t3 <= micros && micros <= t4 && p2 > 0){
                                        int p = a3 - a2;
                                        trans = Math.toIntExact((p * micros / p2) + a2);
                                    }
                                }
                            }
                        }
                    }
                }

                // Color
                for(Map<Tag, String> map : v.getTags()){
                    for(Map.Entry<Tag, String> entry : map.entrySet()){
                        if (Objects.requireNonNull(entry.getKey()) == Tag.OutlineColor) {
                            if (Tag.valuesOf(entry) instanceof Color value) {
                                c = value;
                            }
                        }
                    }
                }
            }
            case Drawing v -> {

            }
            default -> {}
        }

        return new Color(c.getRed(), c.getGreen(), c.getBlue(), trans);
    }

    private static Color doShadowColor(AssEvent event, long micros, Converter converter){
        Color c = event.getStyle().getTextColor().getColor();

        int trans = event.getStyle().getTextColor().getColor().getTransparency();

        switch(converter.getElements().getFirst()){
            case Char v -> {
                // Alpha
                for(Map<Tag, String> map : v.getTags()){
                    for(Map.Entry<Tag, String> entry : map.entrySet()){
                        switch(entry.getKey()){
                            case Tag.ShadowAlpha, Tag.Alpha -> {
                                if(Tag.valuesOf(entry) instanceof Integer value){
                                    trans = value;
                                }
                            }
                        }
                    }
                }

                for(Map<Tag, String> map : v.getTags()){
                    for(Map.Entry<Tag, String> entry : map.entrySet()){
                        switch(entry.getKey()){
                            case Tag.FadeSimple -> {
                                if(Tag.valuesOf(entry) instanceof String[] value){
                                    // Fade in
                                    long s1 = (long) (event.getStart().getMsTime() * 1000);
                                    long t1 = Long.parseLong(value[0]) * 1000;
                                    long s2 = t1 + s1;
                                    // t <> 255
                                    // micros <> x
                                    if(s1 <= micros && micros <= s2 && t1 > 0){
                                        trans = Math.toIntExact(255 * micros / t1);
                                    }

                                    // Fade out
                                    long e4 = (long) (event.getEnd().getMsTime() * 1000);
                                    long t2 = Long.parseLong(value[1]) * 1000;
                                    long e3 = e4 - t2;
                                    // t <> 255
                                    // micros <> x
                                    if(e3 <= micros && micros <= e4 && t2 > 0){
                                        trans = Math.toIntExact(255 * micros / t2);
                                    }
                                }
                            }
                            case Tag.FadeComplex -> {
                                if(Tag.valuesOf(entry) instanceof String[] value){
                                    int a1 = Integer.parseInt(value[0]);
                                    int a2 = Integer.parseInt(value[1]);
                                    int a3 = Integer.parseInt(value[2]);
                                    long t1 = Long.parseLong(value[3]);
                                    long t2 = Long.parseLong(value[4]);
                                    long t3 = Long.parseLong(value[5]);
                                    long t4 = Long.parseLong(value[6]);
                                    long p1 = t2 - t1;
                                    long p2 = t4 - t3;
                                    // Fade in (t <> 255 ; micros <> x)
                                    if(t1 <= micros && micros <= t2 && p1 > 0){
                                        int p = a2 - a1;
                                        trans = Math.toIntExact((p * micros / p1) + a1);
                                    }
                                    // Fade out (t <> 255 ; micros <> x)
                                    if(t3 <= micros && micros <= t4 && p2 > 0){
                                        int p = a3 - a2;
                                        trans = Math.toIntExact((p * micros / p2) + a2);
                                    }
                                }
                            }
                        }
                    }
                }

                // Color
                for(Map<Tag, String> map : v.getTags()){
                    for(Map.Entry<Tag, String> entry : map.entrySet()){
                        if (Objects.requireNonNull(entry.getKey()) == Tag.ShadowColor) {
                            if (Tag.valuesOf(entry) instanceof Color value) {
                                c = value;
                            }
                        }
                    }
                }
            }
            case Drawing v -> {

            }
            default -> {}
        }

        return new Color(c.getRed(), c.getGreen(), c.getBlue(), trans);
    }
}
