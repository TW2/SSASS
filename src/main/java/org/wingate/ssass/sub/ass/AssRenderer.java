package org.wingate.ssass.sub.ass;

import org.wingate.ssass.sub.common.Time;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssRenderer {

    public AssRenderer() {

    }

    public static BufferedImage getImage(
            List<AssEvent> events, List<AssStyle> styles, Time t, int w, int h){

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        AffineTransform transform = new AffineTransform(g.getTransform());

        // Filter every events to fit requirements in activeEvents only
        final List<AssEvent> activeEvents = new ArrayList<>();
        for(AssEvent ev : events){
            if(Time.isInRange(ev.getStart(), ev.getEnd(), t)){
                activeEvents.add(ev);
            }
        }

        // Get dimensions and a basic shape and draw
        Map<AssEvent, List<AssShapeLayout>> sentences = new HashMap<>();
        for(AssEvent ev : activeEvents){
            String textStripped = ev.getText().replaceAll("\\{[^\\}]+\\}", "");
            List<AssShapeLayout> sentenceSize = AssStyle.getShapes(ev.getStyle(), textStripped);
            sentences.put(ev, sentenceSize);
        }

        // For each color
        for(int c=0; c<4; c++){
            // c = 0 shadow color
            // c = 1 karaoke color
            // c = 2 text color
            // c = 3 outline color

            for(Map.Entry<AssEvent, List<AssShapeLayout>> entry : sentences.entrySet()){
                int charsCount = 0;
                for(AssShapeLayout asl : entry.getValue()){
                    // >> c = 0 shadow color

                    charsCount++;
                }
            }

        }


        g.dispose();
        return img;
    }

    //===========================================================

    public void draw(AssEvent event, Time t) {
        final List<Commands> perCharCommands = new ArrayList<>();

        // For one char gets the tags list
        List<CharTags> charTagsList = new ArrayList<>();
        List<String> tags = new ArrayList<>();

        Pattern p = Pattern.compile("(?<cm>\\{?[^\\}]?\\}?)(?<ch>[^\\{\\}]{1})");
        Matcher m = p.matcher(event.getText());

        while(m.find()){
            String[] st = m.group("cm")
                    .replaceAll("\\{", "")
                    .replaceAll("\\}", "")
                    .split("\\\\");
            tags.addAll(Arrays.stream(st).toList());
            String ch = m.group("ch");
            charTagsList.add(new CharTags(ch, tags));
        }

        // Define the state of each character
        Commands lastCommands = new Commands(event.getStyle());
        for(CharTags ct : charTagsList){
            Commands cs = Commands.parse(event, ct, t, lastCommands);
            perCharCommands.add(cs);
            lastCommands = cs;
        }

        // Shape it and drawing

    }

    //===========================================================

    public static void main(String[] args) throws IOException {
        List<AssStyle> styles = new ArrayList<>();
        List<AssEvent> events = new ArrayList<>();

        AssStyle style = new AssStyle("Arial", 52f, Color.white);
        styles.add(style);

        AssEvent event = new AssEvent(
                true, // visible
                0, // layer
                new Time(0d), // start
                new Time(5_000_000d), //end
                style,
                "",
                0d,
                0d,
                0d,
                "",
                "Je vois ou pas, ce que je peux ou pas faire !"
        );
        events.add(event);

        ImageIO.write(
                getImage(events, styles, new Time(2_500_000d), 1280, 720),
                "png",
                new File("test.png")
        );
    }
}
