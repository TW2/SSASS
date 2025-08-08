package org.wingate.ssass.assassin;



import org.wingate.ssass.assa.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Yakuza {

    private Yakuza(){
        // Be a good person! Keep ur 6!
    }

    public static BufferedImage getImage(String file, double ms, int width, int height) throws AssColorException {
        return getImage(ASS.read(file), ms, width, height);
    }

    public static BufferedImage getImage(ASS ass, double ms, int width, int height) throws AssColorException {
        // Prepare image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Initialize Hitman (our big tank)
        List<Hitman> hitmen = new ArrayList<>();

        // Regroup a lot of AssEvent
        List<AssEvent> events = new ArrayList<>();
        for(AssEvent ev : ass.getEvents()){
            if(ev.getType() != AssEvent.Type.Dialogue) continue;
            if(ev.getStart().getMsTime() <= ms && ms < ev.getEnd().getMsTime()){
                events.add(ev);
            }
        }

        // If no event return a transparency image
        if(events.isEmpty()) return image;

        // If one or more events, then let's open a graphics context to draw
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Parse
        for(AssEvent ev : events){
            // Create a hitman
            Hitman hitman = new Hitman(width, height);

            // We need 'g' for measure
            hitman.parse(ass, ms, ev, g);

            // Add configuration
            hitmen.add(hitman);
        }

        // Draw shadow
        for(Hitman hitman : hitmen){
            for(int i=0; i<hitman.getLetters().size(); i++){
                // Apply paint
                g.setPaint(hitman.getShadowPaints().get(i));
                // Set clip
                g.setClip(hitman.getClip());
                // Fill in visible area
                g.fill(hitman.getShadowShapes().get(i));
            }
        }

        // Reset clip to normal
        g.setClip(0, 0, width, height);

        // Draw blur/be
        for(Hitman hitman : hitmen){
            // TODO blur/be
        }

        // Reset clip to normal
        g.setClip(0, 0, width, height);

        // Draw outline
        for(Hitman hitman : hitmen){
            for(int i=0; i<hitman.getLetters().size(); i++){
                // Apply paint
                g.setPaint(hitman.getOutlinePaints().get(i));
                // Set clip
                g.setClip(hitman.getClip());
                // Fill in visible area
                g.fill(hitman.getOutlineShapes().get(i));
            }
        }

        // Reset clip to normal
        g.setClip(0, 0, width, height);

        // Draw karaoke
        for(Hitman hitman : hitmen){
            for(int i=0; i<hitman.getLetters().size(); i++){
                // Apply paint
                g.setPaint(hitman.getKaraokePaints().get(i));
                // Set clip
                g.setClip(hitman.getClip());
                // Fill in visible area
                g.fill(hitman.getKaraokeShapes().get(i));
            }
        }

        // Reset clip to normal
        g.setClip(0, 0, width, height);

        // Draw text
        for(Hitman hitman : hitmen){
            for(int i=0; i<hitman.getLetters().size(); i++){
                // Apply paint
                g.setPaint(hitman.getTextPaints().get(i));
                // Set clip
                g.setClip(hitman.getClip());
                // Fill in visible area
                g.fill(hitman.getTextShapes().get(i));
            }
        }

        // Release graphics context
        g.dispose();
        // Return an image
        return image;
    }
}
