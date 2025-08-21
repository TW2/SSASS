package org.wingate.ssass.assassin;

import org.wingate.ssass.assa.*;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hitman {

    private ASS ass;
    private AssEvent event;
    private double ms;
    private Graphics2D g;
    private final int width;
    private final int height;
    private int letterIndex;
    private final List<Font> fonts;
    // Map tags regroup indices and growing tags
    private final Map<Integer, String> tags;
    // List letter regroup characters (that can use the map)
    private final List<String> letters;

    /**
     * Take into account about:
     * b-i-u-s-fn-fs-r-drawing-bord(copy)-shad(copy)-fe
     */
    private final List<Shape> textShapes;

    /**
     * Take into account about:
     * b-i-u-s-fn-fs-r-drawing-bord(copy)-shad(copy)-fe
     */
    private final List<Shape> karaokeShapes;

    /**
     * Take into account about:
     * b-i-u-s-fn-fs-r-drawing-bord(copy)-shad(copy)-fe
     */
    private final List<Shape> outlineShapes;

    /**
     * Take into account about:
     * b-i-u-s-fn-fs-r-drawing-bord(copy)-shad(copy)-fe
     */
    private final List<Shape> shadowShapes;

    /**
     * Take into account about:
     * an-a-pos-move-pbo-fsp-q
     */
    private Point2D position;

    /**
     * Take into account about:
     * org
     */
    private Point2D origin;

    /**
     * Take into account about:
     * fsc-fa-fr-p
     */
    private final List<Transform3D> transforms;

    /**
     * Take into account about:
     * alpha-1a-c-1c-1pt-fad-fade
     */
    private final List<Paint> textPaints;

    /**
     * Take into account about:
     * alpha-2a-2c-2pt-fad-fade
     */
    private final List<Paint> karaokePaints;

    /**
     * Take into account about:
     * alpha-3a-3c-3pt-fad-fade
     */
    private final List<Paint> outlinePaints;

    /**
     * Take into account about:
     * alpha-4a-4c-4pt-fad-fade
     */
    private final List<Paint> shadowPaints;

    /**
     * Take into account about:
     * clip-k-K-kf-ko
     */
    private Rectangle2D clip;

    public static class Transform3D {
        private double frx;
        private double fry;
        private double frz;
        private double shearX;
        private double shearY;
        private Point2D position;
        private Point2D origin;
        private GeneralPath generalPath;

        public Transform3D(double frx, double fry, double frz,
                           double shearX, double shearY,
                           Point2D position, Point2D origin,
                           GeneralPath generalPath) {
            this.frx = frx;
            this.fry = fry;
            this.frz = frz;
            this.shearX = shearX;
            this.shearY = shearY;
            this.position = position;
            this.origin = origin;
            this.generalPath = generalPath;
        }

        public Shape getShape(){
            Shape shape = generalPath;



            return shape;
        }
    }

    public Hitman(int width, int height) {
        ass = null;
        event = null;
        ms = -1d;
        g = null;
        this.width = width;
        this.height = height;
        letterIndex = 0;
        fonts = new ArrayList<>();

        tags = new HashMap<>();
        letters = new ArrayList<>(); // Letter or drawing

        // ArrayList >> for a shape, shape >> for a letter or drawing
        textShapes = new ArrayList<>();
        karaokeShapes = new ArrayList<>();
        outlineShapes = new ArrayList<>();
        shadowShapes = new ArrayList<>();
        position = new Point2D.Double((double)width /2, (double)height /2);
        origin = new Point2D.Double((double)width /2, (double)height /2);
        transforms = new ArrayList<>();
        textPaints = new ArrayList<>();
        karaokePaints = new ArrayList<>();
        outlinePaints = new ArrayList<>();
        shadowPaints = new ArrayList<>();
        clip = new Rectangle2D.Double(0d, 0d, width, height);
    }

    // Just prepare (parse event) and fill objects (no draw)
    public void parse(ASS ass, double ms, AssEvent event, Graphics2D g) throws AssColorException {
        this.ass = ass;
        this.ms = ms;
        this.event = event;
        this.g = g;

        parseShape(); // The shape for the drawing with all set
        parsePosition(); // Give the position of shape with scale
        parseOrigin(); // Give the origin of rotation to be used in shape
        parseTransform(); // Give the rotation, scale, shear of shape
        parseTextPaint(); // The text paint for the drawing
        parseKaraokePaint(); // The karaoke paint for the drawing
        parseOutlinePaint(); // The outline paint for the drawing
        parseShadowPaint(); // The shadow paint for the drawing
        parseClip(); // The clip to crop for the drawing
    }

    private void parseShape() throws AssColorException {
        // Set \h \n \N with q
        String text = event.getText();
        text = "{\\ASSA}" + text;
        text = text.replace("\\h", " ");
        text = text.replace("\\N", "\n");

        Pattern p; Matcher m;
        p = Pattern.compile("\\{\\\\p(?<scale>\\d+)}(?<draw>[mnlbspc\\s\\d]+)\\{p0}");
        m = p.matcher(text);
        while(m.find()){
            // TODO chainable drawing with general path for each entity
            // TODO example:
            // {\an5\p1}m 0 0 l 0 100 100 100 100 0{\p0}{\p1}m 0 0 l 0 200 200 200 200 0{\p0}
            System.out.println("Drawing");
        }
        // TODO chainable ass drawing with text with recursive with 2 chained functions
        // TODO 1st drawing, 2nd text, then 1st drawing, 2nd text and so on
        // TODO with the whole line
        // {\an5\p1}m 0 0 l 0 100 100 100 100 0{\p0}aaa{\p1}m 0 0 l 0 200 200 200 200 0{\p0}aaa

        if(text.contains("}")){
            StringBuilder list = new StringBuilder();
            String[] array = text.split("\\{");
            int inc = 0;
            for(String s : array){
                String[] t = s.split("}");
                list.append(t[0]);
                try{
                    for(char c : t[1].toCharArray()){
                        letters.add(Character.toString(c));
                        tags.put(inc, list.toString());
                        inc++;
                    }
                }catch(Exception _){

                }
            }
        }else{
            tags.put(0, "");
            for(char c : text.toCharArray()){
                letters.add(Character.toString(c));
            }
        }

        for(int i=0; i<letters.size(); i++){
            letterIndex = i;
            fonts.add(parseFont());
        }

        if(wrapStyle() == 2){
            text = text.replace("\\n", "\n");
        }
        event.setText(text);

        // TODO remove this control code:
        for(Map.Entry<Integer, String> entry : tags.entrySet()){
            System.out.printf("%d: %s, %s\n",
                    entry.getKey(), entry.getValue(), letters.get(entry.getKey()));
        }
    }

    // an-a-pos-move-pbo-fsp-q
    private void parsePosition(){
        // Shaping
        double generalAdvance = 0d;
        double totalWidth;
        double totalHeight = 0d;
        // ASCENT: from top to baseline
        double ascent = 0d;
        // DESCENT: from baseline to leading
        // from top to bottom: line height = ascent + descent + leading
        // Java draws string from baseline
        double descent = 0d;
        double scaleX = 1d;
        double scaleY = 1d;
        for(int i=0; i<letters.size(); i++){
            letterIndex = i;
            TextLayout layout = new TextLayout(
                    letters.get(letterIndex), // String
                    fonts.get(letterIndex), // Font
                    g.getFontRenderContext() // FontRenderContext
            );
            if(scaleXY() != -1d) scaleX = scaleY = scaleXY();
            if(scaleX() != -1d) scaleX = scaleX();
            if(scaleY() != -1d) scaleY = scaleY();
            AffineTransform tr = new AffineTransform();
            tr.scale(scaleX, scaleY);
            tr.translate(generalAdvance * 1 / (scaleX == 0d ? 1d : scaleX), 0d);
            Shape shape = layout.getOutline(tr);
            getTextShapes().add(shape);
            getKaraokeShapes().add(shape);
            getOutlineShapes().add(shape);
            getShadowShapes().add(shape);
            generalAdvance += (fontSpacing() + layout.getAdvance() * scaleX);
            totalHeight = Math.max(totalHeight * scaleY, layout.getBounds().getHeight() * scaleY);
            ascent = Math.max(ascent, layout.getAscent());
            descent = Math.max(descent, layout.getDescent());
        }
        totalWidth = generalAdvance;

        // Alignment of style for this event
        int an = event.getStyle().getAlignment().getNumber();
        if(!tags.get(0).isEmpty()){
            int value;
            // Alignment a
            value = alignLegacy(); if(value != 0) an = value;
            // Alignment an
            value = alignNumpad(); if(value != 0) an = value;
        }

        // Alignment (style) an
        Point2D p = styleAlignment(an, totalWidth, totalHeight,
                ascent * scaleY, descent * scaleY);
        origin = p;

        // Offset calculation pos move
        Point2D pos = position();
        p = new Point2D.Double(p.getX() + pos.getX(), p.getY() + pos.getY());

        // Update shapes
        AffineTransform sh_tr = new AffineTransform();
        sh_tr.translate(p.getX(), p.getY());
        // Text
        textShapes.replaceAll(s -> new GeneralPath(s).createTransformedShape(sh_tr));
        // Karaoke
        karaokeShapes.replaceAll(s -> new GeneralPath(s).createTransformedShape(sh_tr));
        // Outline
        outlineShapes.replaceAll(s -> new GeneralPath(s).createTransformedShape(sh_tr));
        // Shadow
        shadowShapes.replaceAll(s -> new GeneralPath(s).createTransformedShape(sh_tr));

        position = p;
    }

    private void parseOrigin(){
        // a/an > done, here we search for org
        orgOffset();
    }

    private void parseTransform(){
        // fr( |x|y|z), scale (already done), shear
        for(int i=0; i<letters.size(); i++){
            letterIndex = i;
            try{
                bord();
            }catch(Exception _){ }
        }

    }

    private void parseTextPaint(){
        for(int i=0; i<letters.size(); i++){
            letterIndex = i;
            try{
                textPaints.add(textColor());
            }catch(Exception _){ }
        }
    }

    private void parseKaraokePaint(){
        for(int i=0; i<letters.size(); i++){
            letterIndex = i;
            try{
                karaokePaints.add(karaokeColor());
            }catch(Exception _){ }
        }
    }

    private void parseOutlinePaint(){
        for(int i=0; i<letters.size(); i++){
            letterIndex = i;
            try{
                outlinePaints.add(outlineColor());
            }catch(Exception _){ }
        }
    }

    private void parseShadowPaint(){
        for(int i=0; i<letters.size(); i++){
            letterIndex = i;
            try{
                shadowPaints.add(shadowColor());
            }catch(Exception _){ }
        }
    }

    private void parseClip(){

    }

    private Font parseFont() throws AssColorException {
        // r fn fs
        Font notEval = fontFromStyle();
        // Real size in points
        float sizePoints = fontSizePoints(notEval);
        // b i u s
        return fontFromAttributes(notEval, sizePoints);
    }

    // For r fn fs
    private Font fontFromStyle() throws AssColorException {
        // Default line font defined by event style
        Font notEval = event.getStyle().getAssFont().getFont();

        Pattern p; Matcher m;

        // Search for the last font name in tags (\rStyleName or \r)
        if(tags.containsKey(letterIndex)){
            String[] inner = tags.get(letterIndex).split("\\\\");
            for(String tag : inner){
                // RESET
                p = Pattern.compile("r(.*)");
                m = p.matcher(tag);
                while(m.find()){
                    String styleName = m.groupCount() > 1 ? m.group(1) : event.getStyle().getName();
                    for(AssStyle style : ass.getStyles()){
                        if(style.getName().equals(styleName)){
                            // Replace with standard values (not r)
                            tags.put(letterIndex, tags.get(letterIndex).replace("\\r", style.resetToOverrides()));
                            notEval = style.getAssFont().getFont();
                            break;
                        }
                    }
                }

                // FONT NAME
                p = Pattern.compile("fn(.*)");
                m = p.matcher(tag);
                while(m.find()){
                    notEval = new Font(m.group(1), notEval.getStyle(), notEval.getSize());
                }

                // FONT SIZE
                p = Pattern.compile("fs(\\d+.?\\d?)");
                m = p.matcher(tag);
                while(m.find()){
                    notEval = notEval.deriveFont(Float.parseFloat(m.group(1)));
                }
            }
        }
        return notEval.deriveFont(event.getStyle().getAssFont().getSize());
    }

    private float fontSizePoints(Font notEval){
        // Rectangle to find the real size by try and try again resizing pass
        Rectangle2D evaluation = new Rectangle2D.Double(
                0d,
                0d,
                ass.getInfos().getPlayResX() * 2.5,
                event.getStyle().getAssFont().getFont().getSize()
        );

        // Boxing
        TextLayout layout = new TextLayout(
                event.getText(),
                notEval,
                g.getFontRenderContext()
        );

        // Evaluation loop
        float fontSizePoints = notEval.getSize2D();
        double evaluate = evaluation.getBounds2D().getHeight();
        double forced = layout.getAscent() + layout.getLeading() + layout.getDescent();
        while(evaluate < forced && fontSizePoints > 0f){
            fontSizePoints--;
            layout = new TextLayout(
                    letters.get(letterIndex),
                    notEval.deriveFont(fontSizePoints),
                    g.getFontRenderContext()
            );
            forced = layout.getAscent() + layout.getLeading() + layout.getDescent();
        }

        return fontSizePoints;
    }

    private Font fontFromAttributes(Font notEval, float fontSizePoints){
        Map<TextAttribute, Object> attributes = new HashMap<>();
        // Font
        attributes.put(TextAttribute.FONT, notEval);
        attributes.put(TextAttribute.FAMILY, notEval.getFamily());
        // Size Points to pixels
        attributes.put(TextAttribute.SIZE, fontSizePoints);
        // Bold / Plain
        attributes.put(TextAttribute.WEIGHT,
                fontTagsHasBold(event.getStyle().getAssFont().isBold()) ?
                TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
        // Italic / Plain
        attributes.put(TextAttribute.POSTURE,
                fontTagsHasItalic(event.getStyle().getAssFont().isItalic()) ?
                TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);
        // Underline / Plain
        attributes.put(TextAttribute.UNDERLINE,
                fontTagsHasUnderline(event.getStyle().getAssFont().isUnderline()) ?
                TextAttribute.UNDERLINE_ON : -1);
        // StrikeOut / Plain
        attributes.put(TextAttribute.STRIKETHROUGH,
                fontTagsHasStrikeOut(event.getStyle().getAssFont().isStrikeout()) ?
                TextAttribute.STRIKETHROUGH_ON : false);

        // Kerning
        attributes.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
        // Ligatures
        attributes.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);

        return new Font(attributes);
    }

    private boolean fontTagsHasBold(boolean fromStyle){
        boolean value = fromStyle;
        // Search for the last bold in tags (\b1 or \b0)
        if(tags.containsKey(letterIndex)){
            String[] inner = tags.get(letterIndex).split("\\\\");
            for(String tag : inner){
                if(tag.startsWith("b") && tag.length() == 2){
                    value = Integer.parseInt(tag.substring(1)) == 1;
                }
            }
        }
        return value;
    }

    private boolean fontTagsHasItalic(boolean fromStyle){
        boolean value = fromStyle;
        // Search for the last bold in tags (\b1 or \b0)
        if(tags.containsKey(letterIndex)){
            String[] inner = tags.get(letterIndex).split("\\\\");
            for(String tag : inner){
                if(tag.startsWith("i") && tag.length() == 2){
                    value = Integer.parseInt(tag.substring(1)) == 1;
                }
            }
        }
        return value;
    }

    private boolean fontTagsHasUnderline(boolean fromStyle){
        boolean value = fromStyle;
        // Search for the last bold in tags (\b1 or \b0)
        if(tags.containsKey(letterIndex)){
            String[] inner = tags.get(letterIndex).split("\\\\");
            for(String tag : inner){
                if(tag.startsWith("u") && tag.length() == 2){
                    value = Integer.parseInt(tag.substring(1)) == 1;
                }
            }
        }
        return value;
    }

    private boolean fontTagsHasStrikeOut(boolean fromStyle){
        boolean value = fromStyle;
        // Search for the last bold in tags (\b1 or \b0)
        if(tags.containsKey(letterIndex)){
            String[] inner = tags.get(letterIndex).split("\\\\");
            for(String tag : inner){
                if(tag.startsWith("s") && tag.length() == 2){
                    value = Integer.parseInt(tag.substring(1)) == 1;
                }
            }
        }
        return value;
    }

    private double fontSpacing(){
        double value = 0;
        // Search for the last bold in tags (\b1 or \b0)
        if(tags.containsKey(letterIndex)){
            String[] inner = tags.get(letterIndex).split("\\\\");
            for(String tag : inner){
                if(tag.startsWith("fsp") && tag.length() > 3){
                    value = Double.parseDouble(tag.substring(3));
                }
            }
        }
        return value;
    }

    private double scaleX(){
        double value = -100d;
        // Search for the last bold in tags (\b1 or \b0)
        if(tags.containsKey(letterIndex)){
            String[] inner = tags.get(letterIndex).split("\\\\");
            for(String tag : inner){
                if(tag.startsWith("fscx") && tag.length() > 4){
                    value = Double.parseDouble(tag.substring(4));
                }
            }
        }
        return value / 100d;
    }

    private double scaleY(){
        double value = -100d;
        // Search for the last bold in tags (\b1 or \b0)
        if(tags.containsKey(letterIndex)){
            String[] inner = tags.get(letterIndex).split("\\\\");
            for(String tag : inner){
                if(tag.startsWith("fscy") && tag.length() > 4){
                    value = Double.parseDouble(tag.substring(4));
                }
            }
        }
        return value / 100d;
    }

    private double scaleXY(){
        double value = -100d;
        // Search for the last bold in tags (\b1 or \b0)
        if(tags.containsKey(letterIndex)){
            String[] inner = tags.get(letterIndex).split("\\\\");
            Pattern p = Pattern.compile("fsc(\\d+)"); Matcher m;
            for(String tag : inner){
                m = p.matcher(tag);
                while(m.find()){
                    value = Double.parseDouble(m.group(1));
                }
            }
        }
        return value / 100d;
    }

    private int alignLegacy(){
        int value = 0;
        String[] inner = tags.get(0).split("\\\\");
        for(String tag : inner){
            if(tag.startsWith("a") && tag.charAt(1) != 'n'){
                value = Integer.parseInt(tag.substring(1));
                switch(value){
                    case 9 -> value = 4;
                    case 10 -> value = 5;
                    case 11 -> value = 6;
                    case 5 -> value = 7;
                    case 6 -> value = 8;
                    case 7 -> value = 9;
                }
            }
        }
        return value;
    }

    private int alignNumpad(){
        int value = 0;
        String[] inner = tags.get(0).split("\\\\");
        for(String tag : inner){
            if(tag.startsWith("an") && tag.length() == 3){
                value = Integer.parseInt(tag.substring(2));
            }
        }
        return value;
    }

    private int wrapStyle(){
        int value = ass.getInfos().getWrapStyle();
        String[] inner = tags.get(0).split("\\\\");
        for(String tag : inner){
            if(tag.startsWith("q") && tag.length() == 2){
                value = Integer.parseInt(tag.substring(1));
            }
        }
        return value;
    }

    private void orgOffset(){
        // Search org
        Pattern p = Pattern.compile("\\\\org\\((?<x>\\d+.?\\d?),(?<y>\\d+.?\\d?)\\)");
        Matcher m = p.matcher(tags.get(0));
        while(m.find()){
            origin = new Point2D.Double(
                    Double.parseDouble(m.group("x")),
                    Double.parseDouble(m.group("y"))
            );
        }
    }

    private Color textColor() throws AssColorException {
        AssColor value = new AssColor(Color.white);
        // Search for the last bold in tags (\b1 or \b0)
        if(tags.containsKey(letterIndex)){
            String[] inner = tags.get(letterIndex).split("\\\\");
            for(String tag : inner){
                if(tag.startsWith("c&H") && tag.length() > 3){
                    value = AssColor.fromScheme(tag.substring(3, 9), AssColor.Scheme.BGR);
                }else if(tag.startsWith("1c&H") && tag.length() > 4){
                    value = AssColor.fromScheme(tag.substring(4, 10), AssColor.Scheme.BGR);
                }
            }
            for(String tag : inner){
                if(tag.startsWith("alpha&H") && tag.length() > 7){
                    int alpha = Integer.parseInt(tag.substring(7, 9), 16);
                    value = new AssColor(value.getColor(), alpha);
                }else if(tag.startsWith("1a&H") && tag.length() > 4){
                    int alpha = Integer.parseInt(tag.substring(4, 6), 16);
                    value = new AssColor(value.getColor(), alpha);
                }
            }
        }
        return value.getColor();
    }

    private Color karaokeColor() throws AssColorException {
        AssColor value = new AssColor(Color.yellow);
        // Search for the last bold in tags (\b1 or \b0)
        if(tags.containsKey(letterIndex)){
            String[] inner = tags.get(letterIndex).split("\\\\");
            for(String tag : inner){
                if(tag.startsWith("2c&H") && tag.length() > 4){
                    value = AssColor.fromScheme(tag.substring(4, 10), AssColor.Scheme.BGR);
                }
            }
            for(String tag : inner){
                if(tag.startsWith("alpha&H") && tag.length() > 7){
                    int alpha = Integer.parseInt(tag.substring(7, 9), 16);
                    value = new AssColor(value.getColor(), alpha);
                }else if(tag.startsWith("2a&H") && tag.length() > 4){
                    int alpha = Integer.parseInt(tag.substring(4, 6), 16);
                    value = new AssColor(value.getColor(), alpha);
                }
            }
        }
        return value.getColor();
    }

    private Color outlineColor() throws AssColorException {
        AssColor value = new AssColor(Color.black);
        // Search for the last bold in tags (\b1 or \b0)
        if(tags.containsKey(letterIndex)){
            String[] inner = tags.get(letterIndex).split("\\\\");
            for(String tag : inner){
                if(tag.startsWith("3c&H") && tag.length() > 4){
                    value = AssColor.fromScheme(tag.substring(4, 10), AssColor.Scheme.BGR);
                }
            }
            for(String tag : inner){
                if(tag.startsWith("alpha&H") && tag.length() > 7){
                    int alpha = Integer.parseInt(tag.substring(7, 9), 16);
                    value = new AssColor(value.getColor(), alpha);
                }else if(tag.startsWith("3a&H") && tag.length() > 4){
                    int alpha = Integer.parseInt(tag.substring(4, 6), 16);
                    value = new AssColor(value.getColor(), alpha);
                }
            }
        }
        return value.getColor();
    }

    private Color shadowColor() throws AssColorException {
        AssColor value = new AssColor(Color.black);
        // Search for the last bold in tags (\b1 or \b0)
        if(tags.containsKey(letterIndex)){
            String[] inner = tags.get(letterIndex).split("\\\\");
            for(String tag : inner){
                if(tag.startsWith("4c&H") && tag.length() > 4){
                    value = AssColor.fromScheme(tag.substring(4, 10), AssColor.Scheme.BGR);
                }
            }
            for(String tag : inner){
                if(tag.startsWith("alpha&H") && tag.length() > 7){
                    int alpha = Integer.parseInt(tag.substring(7, 9), 16);
                    value = new AssColor(value.getColor(), alpha);
                }else if(tag.startsWith("4a&H") && tag.length() > 4){
                    int alpha = Integer.parseInt(tag.substring(4, 6), 16);
                    value = new AssColor(value.getColor(), alpha);
                }
            }
        }
        return value.getColor();
    }

    private Point2D position(){
        Map<Integer, Point2D> points = new HashMap<>();
        Pattern p; Matcher m;
        // Search pos
        p = Pattern.compile("\\\\pos\\((?<x>\\d+.?\\d?),(?<y>\\d+.?\\d?)\\)");
        m = p.matcher(tags.get(0));
        while(m.find()){
            points.put(m.regionStart(), new Point2D.Double(
                    Double.parseDouble(m.group("x")),
                    Double.parseDouble(m.group("y"))
            ));
        }
        // Search move
        p = Pattern.compile("\\\\move\\((?<x1>\\d+.?\\d?),(?<y1>\\d+.?\\d?),(?<x2>\\d+.?\\d?),(?<y2>\\d+.?\\d?),*(?<t1>\\d*),*(?<t2>\\d*)\\)");
        m = p.matcher(tags.get(0));
        while(m.find()){
            double x1 = Double.parseDouble(m.group("x1"));
            double y1 = Double.parseDouble(m.group("y1"));
            double x2 = Double.parseDouble(m.group("x2"));
            double y2 = Double.parseDouble(m.group("y2"));
            double s = m.group("t1").isEmpty() ?
                    event.getStart().getMsTime() : Double.parseDouble(m.group("t1"));
            double e = m.group("t2").isEmpty() ?
                    event.getEnd().getMsTime() : Double.parseDouble(m.group("t2"));

            double distanceX = x2 - x1;
            double distanceY = y2 - y1;
            double totalTime = e - s;

            double currentTimeOffset = e - ms;
            if(currentTimeOffset == 0d) currentTimeOffset = 1d; // Protect

            double x = totalTime / currentTimeOffset * distanceX + x1;
            double y = totalTime / currentTimeOffset * distanceY + y1;

            points.put(m.regionStart(), new Point2D.Double(x, y));
        }
        // If there is nothing in the map then we return default value
        if(points.isEmpty()) return new Point2D.Double();

        // Search for the latest (some people do anything wrong with tags
        // so we have to search for the latest entry which can be pos or move)
        Point2D value = new Point2D.Double();
        // Sort the Integer to get the largest at the end
        for(Map.Entry<Integer, Point2D> entry : points.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()){
            value = entry.getValue();
        }

        // Submit the end value
        return value;
    }

    private Point2D styleAlignment(int an, double sentenceWidth, double sentenceHeight,
               double ascent, double descent){
        // Style
        AssStyle style = event.getStyle();

        // Margins
        double ml = event.getMarginL() > 0 ? event.getMarginL() : style.getMarginL();
        double mr = event.getMarginR() > 0 ? event.getMarginR() : style.getMarginR();
        double mv = event.getMarginV() > 0 ? event.getMarginV() : style.getMarginV();

        // Available distance
        double visibleWidth = width - ml - mr - 10;
        double visibleHeight = height - (2 * mv);

        // Margins offset :
        // left = ml
        // right = width - mr
        // top = mv
        // bottom = height - mv

        // Coordinates to put the first char
        double x = 0d;
        double y = 0d;

        // ascent descent on y (123 and 789)

        switch(an){
            case 1 -> {
                x = ml;
                y = mv + visibleHeight - descent;
            }
            case 2 -> {
                x = ml + (visibleWidth / 2d) - (sentenceWidth / 2d);
                y = mv + visibleHeight - descent;
            }
            case 3 -> {
                x = (ml + visibleWidth - sentenceWidth);
                y = mv + visibleHeight - descent;
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
                y = mv + ascent;
            }
            case 8 -> {
                x = ml + (visibleWidth / 2d) - (sentenceWidth / 2d);
                y = mv + ascent;
            }
            case 9 -> {
                x = (ml + visibleWidth - sentenceWidth);
                y = mv + ascent;
            }
        }

        return new Point2D.Double(x, y);
    }

    private void bord(){
        double value = event.getStyle().getOutline();
        String type = "xy";
        if(tags.containsKey(letterIndex)){
            String[] inner = tags.get(letterIndex).split("\\\\");
            Pattern p = Pattern.compile("(?<type>[xy]*)bord(?<value>\\d+)");
            Matcher m;
            for(String tag : inner){
                m = p.matcher(tag);
                while(m.find()){
                    if(m.groupCount() == 2){
                        type = m.group("type");
                    }
                    value = Double.parseDouble(m.group("value"));
                }
            }
        }

        // Transform
        List<GeneralPath> paths = getPaths(textShapes.get(letterIndex));
        PathType mainPathType = null;
        if(!paths.isEmpty()){
            mainPathType = new PathType(paths.getFirst());
            for(GeneralPath path : paths){
                PathType pt = new PathType(path);
                pathTyper(mainPathType, pt);
            }
        }

        if(mainPathType != null){
            Area area = reshape(mainPathType, null);

            Area result = new Area();
            switch(type){
                case "x" -> result = xBorder(mainPathType, value);
                case "y" -> result = yBorder(mainPathType, value);
                case "xy" -> result = xyBorder(mainPathType, value);
            }

            outlineShapes.set(letterIndex, result);
        }
    }

    private List<GeneralPath> getPaths(Shape shape){
        GeneralPath gp = new GeneralPath(shape);
        double[] coordinates = new double[6];
        PathIterator pi = gp.getPathIterator(null);
        List<GeneralPath> shapes = new ArrayList<>();
        GeneralPath sh = null;
        while(!pi.isDone()){
            int segment = pi.currentSegment(coordinates);
            switch(segment){
                case PathIterator.SEG_MOVETO -> {
                    if(sh != null) {
                        sh.closePath();
                        shapes.add(sh);
                    }
                    sh = new GeneralPath();
                    sh.moveTo(coordinates[0], coordinates[1]);
                }
                case PathIterator.SEG_LINETO -> {
                    if(sh == null){
                        sh = new GeneralPath();
                        sh.moveTo(coordinates[0], coordinates[1]);
                    }
                    sh.lineTo(coordinates[0], coordinates[1]);
                }
                case PathIterator.SEG_QUADTO -> {
                    if(sh == null){
                        sh = new GeneralPath();
                        sh.moveTo(coordinates[0], coordinates[1]);
                    }
                    sh.quadTo(
                            coordinates[0], coordinates[1],
                            coordinates[2], coordinates[3]
                    );
                }
                case PathIterator.SEG_CUBICTO -> {
                    if(sh == null){
                        sh = new GeneralPath();
                        sh.moveTo(coordinates[0], coordinates[1]);
                    }
                    sh.curveTo(
                            coordinates[0], coordinates[1],
                            coordinates[2], coordinates[3],
                            coordinates[4], coordinates[5]
                    );
                }
                case PathIterator.SEG_CLOSE -> {
                    if(sh != null){
                        sh.closePath();
                        shapes.add(sh);
                        sh = null;
                    }
                }
            }
            pi.next();
        }
        return shapes;
    }

    static class PathType {
        public enum Type { Outer, Inner }
        private Type type;
        private final GeneralPath shape;
        private final List<PathType> children;

        public PathType(GeneralPath shape) {
            this.shape = shape;
            children = new ArrayList<>();
            type = Type.Outer;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public GeneralPath getShape() {
            return shape;
        }

        public List<PathType> getChildren() {
            return children;
        }
    }

    private void pathTyper(PathType parent, PathType test){
        Rectangle2D rParent = parent.getShape().getBounds2D();
        Rectangle2D rTest = test.getShape().getBounds2D();
        if(rParent.equals(rTest)) return;
        if(rParent.contains(rTest)){
            test.setType(parent.getType() == PathType.Type.Outer ?
                    PathType.Type.Inner : PathType.Type.Outer);
            parent.getChildren().add(test);
        }else{
            for(PathType path : parent.getChildren()){
                // Recursive here
                pathTyper(path, test);
            }
        }
    }

    private Area reshape(PathType parent, Area main){
        if(main == null){
            main = new Area(parent.getShape());
        }
        System.out.printf("Type[%s]: %s \n",letters.get(letterIndex), parent.getType());
        for(PathType child : parent.getChildren()){
            System.out.printf("Sub[%s]: %s \n",letters.get(letterIndex), child.getType());
            Area c = new Area(child.getShape());
            switch(child.getType()){
                case Outer -> main.add(c);
                case Inner -> main.subtract(c);
            }
            // Recursive here
            main = reshape(child, main);
        }
        return main;
    }

    private Area xBorder(PathType p, double unit){
        Area main = new Area(p.getShape());
        Area a1L = new Area(p.getShape());
        Area a1R = new Area(p.getShape());
        Area a2L = new Area(p.getShape());
        Area a2R = new Area(p.getShape());
        switch(p.getType()){
            case Outer -> {
                System.out.println("Outer");
                // To the left
                // On décale tous les points vers la gauche
                // On supprime tous les points à l'intérieur du parent
                // TODO more precision
                AffineTransform left;
                for(int i=0; i<unit; i++){
                    left = new AffineTransform();
                    left.translate(-1d, 0d);
                    a1L = a1L.createTransformedArea(left);
                    main.add(a1L);
                }

                // To the right
                // On décale tous les points vers la droite
                // On supprime tous les points à l'intérieur du parent
                // TODO more precision
                AffineTransform right;
                for(int i=0; i<unit; i++){
                    right = new AffineTransform();
                    right.translate(1d, 0d);
                    a1R = a1R.createTransformedArea(right);
                    main.add(a1R);
                }
            }
            case Inner -> {
                System.out.println("Inner");
                // Inverse the left
                // On décale tous les points vers la gauche
                // On supprime tous les points à l'extérieur du parent
                // TODO more precision
                AffineTransform left;
                for(int i=0; i<unit; i++){
                    left = new AffineTransform();
                    left.translate(1d, 0d);
                    a2L = a2L.createTransformedArea(left);
                    main.subtract(a2L);
                }

                // Inverse the right
                // On décale tous les points vers la droite
                // On supprime tous les points à l'extérieur du parent
                // TODO more precision
                AffineTransform right;
                for(int i=0; i<unit; i++){
                    right = new AffineTransform();
                    right.translate(-1d, 0d);
                    a2R = a2R.createTransformedArea(right);
                    main.subtract(a2R);
                }
            }
        }
        for(PathType child : p.getChildren()){
            // Recursive here
            xBorder(child, unit);
        }

        return main;
    }

    private Area yBorder(PathType p, double unit){
        switch(p.getType()){
            case Outer -> {
                // To the top
                // On décale tous les points vers le haut
                // On supprime tous les points à l'intérieur du parent

                // To the bottom
                // On décale tous les points vers le bas
                // On supprime tous les points à l'intérieur du parent

            }
            case Inner -> {
                // Inverse the top
                // On décale tous les points vers le haut
                // On supprime tous les points à l'extérieur du parent

                // Inverse the bottom
                // On décale tous les points vers le bas
                // On supprime tous les points à l'extérieur du parent

            }
        }

        for(PathType child : p.getChildren()){
            // Recursive here
            yBorder(child, unit);
        }
        return new Area();
    }

    private Area xyBorder(PathType p, double unit){
        switch(p.getType()){
            case Outer -> {

            }
            case Inner -> {
//
            }
        }

        for(PathType child : p.getChildren()){
            // Recursive here
            xyBorder(child, unit);
        }
        return new Area();
    }

    public List<String> getLetters() {
        return letters;
    }

    public List<Shape> getTextShapes() {
        return textShapes;
    }

    public List<Shape> getKaraokeShapes() {
        return karaokeShapes;
    }

    public List<Shape> getOutlineShapes() {
        return outlineShapes;
    }

    public List<Shape> getShadowShapes() {
        return shadowShapes;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public Point2D getOrigin() {
        return origin;
    }

    public void setOrigin(Point2D origin) {
        this.origin = origin;
    }

    public List<Transform3D> getTransforms() {
        return transforms;
    }

    public List<Paint> getTextPaints() {
        return textPaints;
    }

    public List<Paint> getKaraokePaints() {
        return karaokePaints;
    }

    public List<Paint> getOutlinePaints() {
        return outlinePaints;
    }

    public List<Paint> getShadowPaints() {
        return shadowPaints;
    }

    public Rectangle2D getClip() {
        return clip;
    }

    public void setClip(Rectangle2D clip) {
        this.clip = clip;
    }
}
