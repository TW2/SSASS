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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                // Outline
                g.setColor(event.getStyle().getOutlineColor().getColor());
                c.draw(g);
                // Text
                g.setColor(event.getStyle().getTextColor().getColor());
                c.fill(g);
                tr.translate(c.getAdvance() + c.getExtraSpacing(), 0d);
                g.setTransform(tr);
                // TODO Karaoke
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
}
