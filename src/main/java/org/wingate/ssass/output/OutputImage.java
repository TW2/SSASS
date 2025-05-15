package org.wingate.ssass.output;

import org.wingate.ssass.ass.ASS;
import org.wingate.ssass.ass.AssEvent;
import org.wingate.ssass.render.AGraphicElement;
import org.wingate.ssass.render.Char;
import org.wingate.ssass.render.Converter;

import java.awt.*;
import java.awt.geom.AffineTransform;
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
            images.put(event.getLayer(), doImage(ass, event, width, height));
        }

        return images;
    }

    public static BufferedImage getImage(
            ASS ass, AssEvent event, int width, int height) {
        return doImage(ass, event, width, height);
    }

    private static BufferedImage doImage(ASS ass, AssEvent event, int width, int height){
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
        );

        Converter converter = Converter.creteShapes(event, ass);
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

        // TODO Position (here is in center)
        AffineTransform tr = new AffineTransform();
        tr.translate(
                (width - sentenceWidth) / 2d, // Center X
                ((height - sentenceHeight) / 2d) + sentenceHeight // Center Y
        );
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
}
