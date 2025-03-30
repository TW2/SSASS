package org.wingate.ssass.sub.ass;

import org.wingate.ssass.sub.common.Time;
import org.wingate.ssass.sub.common.Unit;

import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commands implements Cloneable {

    private int videoWidth = 1280;
    private int videoHeight = 720;
    private Shape visibility = null;
    private AssStyle assStyle;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private boolean strikeOut;
    private String fontName;
    private float fontSize;
    private Color textColor;
    private Color karaokeColor;
    private Color outlineColor;
    private Color shadowColor;
    private double alpha;
    private double textAlpha;
    private double karaokeAlpha;
    private double outlineAlpha;
    private double shadowAlpha;
    private double scaleX;
    private double scaleY;
    private double spacing;
    private double angleX;
    private double angleY;
    private double angleZ;
    private int borderStyle;
    private double outlineThicknessX;
    private double outlineThicknessY;
    private double shadowShiftX;
    private double shadowShiftY;
    private double blurEdge;
    private double blur;
    private int alignment;
    private double marginL;
    private double marginR;
    private double marginV;
    private double marginT;
    private double marginB;
    private int encoding;
    private Point2D origin;
    private double shearX;
    private double shearY;
    private Time karaoke;
    private Time karaokeFill;
    private Time karaokeOutline;
    private int wrapStyle;
    private Point2D position;

    // TODO: Animation \t
    // TODO: Visibility \clip
    // TODO: Drawing
    // TODO: Baseline offset \pbo


    /**
     * Define a state
     * @param style ASS style for this state
     */
    public Commands(AssStyle style) {
        assStyle = style;
        bold = style.isBold(); // STYLE: true is 1, false is 0
        italic = style.isItalic(); // STYLE: true is 1, false is 0
        underline = style.isUnderline(); // STYLE: true is 1, false is 0
        strikeOut = style.isStrikeOut(); // STYLE: true is 1, false is 0
        fontName = style.getFontName();
        fontSize = style.getFontSize(); // SIZE: must be the size of height in pixels (TODO)
        textColor = style.getTextColor();
        karaokeColor = style.getKaraokeColor();
        outlineColor = style.getOutlineColor();
        shadowColor = style.getShadowColor();
        alpha = 1d; // ALPHA: 255 is transparent, 0 is opaque (must be the inverse (1d is opaque))
        textAlpha = 1d; // ALPHA: 255 is transparent, 0 is opaque (must be the inverse (1d is opaque))
        karaokeAlpha = 1d; // ALPHA: 255 is transparent, 0 is opaque (must be the inverse (1d is opaque))
        outlineAlpha = 1d; // ALPHA: 255 is transparent, 0 is opaque (must be the inverse (1d is opaque))
        shadowAlpha = 1d; // ALPHA: 255 is transparent, 0 is opaque (must be the inverse (1d is opaque))
        scaleX = 1d; // SCALE: 100 is 1d (100%)
        scaleY = 1d; // SCALE: 100 is 1d (100%)
        spacing = 0d;
        angleX = 0d;
        angleY = 0d;
        angleZ = 0d;
        borderStyle = 1; // TODO: Something to define it, and use it
        outlineThicknessX = 2d; // Bord: in pixel
        outlineThicknessY = 2d; // Bord: in pixel
        shadowShiftX = 2d; // Shad: in pixel
        shadowShiftY = 2d; // Shad: in pixel
        blurEdge = 0d;
        blur = 0d;
        alignment = 2;
        marginL = 0d;
        marginR = 0d;
        marginV = 0d;
        marginT = 0d;
        marginB = 0d;
        encoding = 1; // TODO: Something to define it, and use it
        origin = new Point2D.Double(Double.MIN_VALUE, Double.MAX_VALUE); // Set to min (undesirable value)
        shearX = 0d;
        shearY = 0d;
        karaoke = new Time(-1L);;
        karaokeFill = new Time(-1L);;
        karaokeOutline = new Time(-1L);;
        wrapStyle = 2; // TODO: Something to define it, and use it
        position = new Point2D.Double(Double.MIN_VALUE, Double.MAX_VALUE); // Set to min (undesirable value)
    }

    /**
     * Define a state with Default ASS style
     */
    public Commands() {
        this(new AssStyle());
    }

    /**
     * Try to find some commands to define a state for one character
     *
     * @param event current event
     * @param tt the tags to parse and the character to treat
     * @param t the time to precisely define this state
     * @param lastCommands last commands, may be <tt>null</tt>
     * @return A state with parsed commands of character
     */
    public static Commands parse(AssEvent event, CharTags tt, Time t, Commands lastCommands) {
        Commands cs = lastCommands == null ? new Commands(event.getStyle()) : lastCommands.clone();

        for(String tag : tt.tags()){
            switch(AssTags.search(tag)){
                case Reset -> {
                    // TODO: Get initial Commands from style
                }
                case Bold -> cs.setBold(tag.endsWith("1"));
                case Italic -> cs.setItalic(tag.endsWith("1"));
                case Underline -> cs.setUnderline(tag.endsWith("1"));
                case StrikeOut -> cs.setStrikeOut(tag.endsWith("1"));
                case OutlineThickness -> {
                    cs.setOutlineThicknessX(thicknessOf(tag, cs.getOutlineThicknessX()));
                    cs.setOutlineThicknessY(thicknessOf(tag, cs.getOutlineThicknessY()));
                }
                case OutlineThicknessX -> cs.setOutlineThicknessX(thicknessOf(tag, cs.getOutlineThicknessX()));
                case OutlineThicknessY -> cs.setOutlineThicknessY(thicknessOf(tag, cs.getOutlineThicknessY()));
                case ShadowShift -> {
                    cs.setShadowShiftX(shiftOf(tag, cs.getShadowShiftX()));
                    cs.setShadowShiftY(shiftOf(tag, cs.getShadowShiftY()));
                }
                case ShadowShiftX -> cs.setShadowShiftX(shiftOf(tag, cs.getShadowShiftX()));
                case ShadowShiftY -> cs.setShadowShiftY(shiftOf(tag, cs.getShadowShiftY()));
                case BlurEdge -> cs.setBlurEdge(blurEdgeOf(tag, cs.getBlurEdge()));
                case Blur -> { cs.setBlur(blurOf(tag, cs.getBlur())); }
                case FontName -> cs.setFontName(fontNameOf(tag, cs.getFontName()));
                case FontSize -> cs.setFontSize(fontSizeOf(tag, cs.getFontSize()));
                case FontEncoding -> {
                    // TODO: Font encoding
                }
                case Scale -> {
                    cs.setScaleX(scaleOf(tag, cs.getScaleX()));
                    cs.setScaleY(scaleOf(tag, cs.getScaleY()));
                }
                case ScaleX -> cs.setScaleX(scaleOf(tag, cs.getScaleX()));
                case ScaleY -> cs.setScaleY(scaleOf(tag, cs.getScaleY()));
                case Spacing -> cs.setSpacing(spacingOf(tag, cs.getSpacing()));
                case RotationX -> cs.setAngleX(angleOf(tag, cs.getAngleX()));
                case RotationY -> cs.setAngleY(angleOf(tag, cs.getAngleY()));
                case RotationZ, Rotation -> cs.setAngleZ(angleOf(tag, cs.getAngleZ()));
                case ShearX -> cs.setShearX(shearOf(tag, cs.getShearX()));
                case ShearY -> cs.setShearY(shearOf(tag, cs.getShearY()));
                case TextColor, PrimaryColor -> cs.setTextColor(colorOf(tag, cs.getTextColor()));
                case KaraokeColor -> cs.setKaraokeColor(colorOf(tag, cs.getKaraokeColor()));
                case OutlineColor -> cs.setOutlineColor(colorOf(tag, cs.getOutlineColor()));
                case ShadowColor -> cs.setShadowColor(colorOf(tag, cs.getShadowColor()));
                case Alpha -> {
                    double v = alphaOf(tag, cs.getAlpha());
                    cs.setAlpha(v);
                    cs.setTextAlpha(v);
                    cs.setKaraokeAlpha(v);
                    cs.setOutlineAlpha(v);
                    cs.setShadowAlpha(v);
                }
                case TextAlpha -> cs.setTextAlpha(alphaOf(tag, cs.getTextAlpha()));
                case KaraokeAlpha -> cs.setKaraokeAlpha(alphaOf(tag, cs.getKaraokeAlpha()));
                case OutlineAlpha -> cs.setOutlineAlpha(alphaOf(tag, cs.getOutlineAlpha()));
                case ShadowAlpha -> cs.setShadowAlpha(alphaOf(tag, cs.getShadowAlpha()));
                case AlignmentLegacy, AlignmentNumPad -> cs.setAlignment(alignOf(tag, cs.getAlignment()));
                case BorderStyle -> cs.setBorderStyle(borderStyleOf(tag, cs.getBorderStyle()));
                case MarginL -> cs.setMarginL(marginOf(tag, cs.getMarginL()));
                case MarginR -> cs.setMarginR(marginOf(tag, cs.getMarginR()));
                case MarginV -> cs.setMarginV(marginOf(tag, cs.getMarginV()));
                case MarginT -> cs.setMarginT(marginOf(tag, cs.getMarginT()));
                case MarginB -> cs.setMarginB(marginOf(tag, cs.getMarginB()));
                case Origin -> cs.setOrigin(originOf(tag, cs.getOrigin()));
                case Position -> cs.setPosition(positionOf(tag, cs.getPosition()));
                case Movement -> cs.setPosition(moveOf(tag, cs.getPosition(), event, t));
                case WrapStyle -> {
                    // TODO: Wrap style
                }
                case FadeSimple -> {
                    double v = fadOf(tag, cs.getAlpha(), event, t);
                    cs.setAlpha(v);
                    cs.setTextAlpha(v);
                    cs.setKaraokeAlpha(v);
                    cs.setOutlineAlpha(v);
                    cs.setShadowAlpha(v);
                }
                case FadeComplex -> {
                    double v = fadeOf(tag, cs.getAlpha(), event, t);
                    cs.setAlpha(v);
                    cs.setTextAlpha(v);
                    cs.setKaraokeAlpha(v);
                    cs.setOutlineAlpha(v);
                    cs.setShadowAlpha(v);
                }
                case ClipRectangle, ClipDrawing -> cs.setVisibility(clipOf(tag, cs.getVideoWidth(), cs.getVideoHeight()));
                case InvisibleClipRectangle, InvisibleClipDrawing -> cs.setVisibility(invisibleClipOf(tag, cs.getVideoWidth(), cs.getVideoHeight()));
                case Karaoke -> {
                    // TODO: Karaoke
                }
                case KaraokeFillLegacy, KaraokeFill -> {
                    // TODO: Karaoke Fill
                }
                case KaraokeOutline -> {
                    // TODO: Karaoke Outline
                }
                case Animation -> {
                    // TODO: Animation
                }
                case Drawing -> {
                    // TODO: Drawing
                }
                case BaselineOffset -> {
                    // TODO: Baseline Offset
                }

            }
        }

        return cs;
    }

    private static double thicknessOf(String tag, double defVal){
        double v = defVal;
        Pattern p = Pattern.compile("[xy]?bord(?<v>\\d+.*\\d*)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = Double.parseDouble(m.group("v"));
        }
        return v;
    }

    private static double shiftOf(String tag, double defVal){
        double v = defVal;
        Pattern p = Pattern.compile("[xy]?shad(?<v>\\d+.*\\d*)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = Double.parseDouble(m.group("v"));
        }
        return v;
    }

    private static double blurEdgeOf(String tag, double defVal){
        double v = defVal;
        Pattern p = Pattern.compile("be(?<v>\\d+.*\\d*)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = Double.parseDouble(m.group("v"));
        }
        return v;
    }

    private static double blurOf(String tag, double defVal){
        double v = defVal;
        Pattern p = Pattern.compile("blur(?<v>\\d+.*\\d*)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = Double.parseDouble(m.group("v"));
        }
        return v;
    }

    private static String fontNameOf(String tag, String defVal){
        String v = defVal;
        Pattern p = Pattern.compile("fn(?<v>.+)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = m.group("v");
        }
        return v;
    }

    private static float fontSizeOf(String tag, float defVal){
        float v = defVal;
        Pattern p = Pattern.compile("fs(?<v>\\d+.*\\d*)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = Float.parseFloat(m.group("v"));
        }
        return v;
    }

    private static double scaleOf(String tag, double defVal){
        double v = defVal;
        Pattern p = Pattern.compile("fsc[xy]?(?<v>\\d+.*\\d*)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = Double.parseDouble(m.group("v"));
        }
        return v;
    }

    private static double spacingOf(String tag, double defVal){
        double v = defVal;
        Pattern p = Pattern.compile("fsp(?<v>\\d+.*\\d*)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = Double.parseDouble(m.group("v"));
        }
        return v;
    }

    private static double angleOf(String tag, double defVal){
        double v = defVal;
        Pattern p = Pattern.compile("fr[xyz]?(?<v>\\d+.*\\d*)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = Double.parseDouble(m.group("v"));
        }
        return v;
    }

    private static double shearOf(String tag, double defVal){
        double v = defVal;
        Pattern p = Pattern.compile("fa[xy](?<v>\\d+.*\\d*)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = Double.parseDouble(m.group("v"));
        }
        return v;
    }

    private static Color colorOf(String tag, Color defVal){
        Color color = defVal;
        Pattern p = Pattern.compile("&H(?<b>[a-f0-9]{2})(?<g>[a-f0-9]{2})(?<r>[a-f0-9]{2})");
        Matcher m = p.matcher(tag);
        if(m.find()){
            color = new Color(
                    Integer.parseInt(m.group("r"), 16),
                    Integer.parseInt(m.group("g"), 16),
                    Integer.parseInt(m.group("b"), 16)
            );
        }
        return color;
    }

    private static double alphaOf(String tag, double defVal){
        double v = defVal;
        Pattern p = Pattern.compile("&H(?<a>[a-f0-9]{2})");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = (double)(255 - Integer.parseInt(m.group("a"), 16)) / 255d;
        }
        return v;
    }

    private static Color colorFromAlphaOf(Color c, double v){
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(v * 255));
    }

    private static int alignOf(String tag, int defVal){
        int v = defVal;
        Pattern p = Pattern.compile("(?<t>an?)(?<v>\\d)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            int size = m.group("t").length();
            v = Integer.parseInt(m.group("v"));
            if(size == 2) return v;
            switch(v){
                case 5 -> v = 7;
                case 6 -> v = 8;
                case 7 -> v = 9;
                case 9 -> v = 4;
                case 10 -> v = 5;
                case 11 -> v = 6;
            }
        }
        return v;
    }

    private static int borderStyleOf(String tag, int defVal){
        int v = defVal;
        Pattern p = Pattern.compile("bs(?<v>\\d)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = Integer.parseInt(m.group("v"));
        }
        return v;
    }

    private static double marginOf(String tag, double defVal){
        double v = defVal;
        Pattern p = Pattern.compile("m[lrvtb](?<v>\\d+.*\\d*)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = Double.parseDouble(m.group("v"));
        }
        return v;
    }

    private static Point2D originOf(String tag, Point2D defVal){
        Point2D v = defVal;
        Pattern p = Pattern.compile("org\\((?<x>\\d+.*\\d*),(?<y>\\d+.*\\d*)\\)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = new Point2D.Double(
                    Double.parseDouble(m.group("x")),
                    Double.parseDouble(m.group("y"))
            );
        }
        return v;
    }

    private static Point2D positionOf(String tag, Point2D defVal){
        Point2D v = defVal;
        Pattern p = Pattern.compile("pos\\((?<x>\\d+.*\\d*),(?<y>\\d+.*\\d*)\\)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            v = new Point2D.Double(
                    Double.parseDouble(m.group("x")),
                    Double.parseDouble(m.group("y"))
            );
        }
        return v;
    }

    private static Point2D moveOf(String tag, Point2D defVal, AssEvent event, Time t){
        Pattern p = Pattern.compile("move\\(" +
                "(?<x1>\\d+.*\\d*),(?<y1>\\d+.*\\d*)," +
                "(?<x2>\\d+.*\\d*),(?<y2>\\d+.*\\d*),*" +
                "(?<t1>\\d*),(?<t2>\\d*)\\)");
        Matcher m = p.matcher(tag);
        double x1 = defVal.getX(), x2 = defVal.getX();
        double y1 = defVal.getY(), y2 = defVal.getY();
        Time t1 = event.getStart();
        Time t2 = event.getEnd();
        if(m.find()){
            x1 = Double.parseDouble(m.group("x1"));
            x2 = Double.parseDouble(m.group("x2"));
            y1 = Double.parseDouble(m.group("y1"));
            y2 = Double.parseDouble(m.group("y2"));
            if(m.groupCount() == 6){
                t1 = Time.add(t1, Time.from(Long.parseLong(m.group("t1")), Unit.Millisecond));
                t2 = Time.subtract(t2, Time.from(Long.parseLong(m.group("t2")), Unit.Millisecond));
            }
        }
        Time diff = Time.subtract(t2, t1);
        if(diff.getMicros() == 0d) return defVal;
        Time ref = Time.subtract(t, t1);
        double coefficient = ref.getMicros() / diff.getMicros();
        double x = x1 + (x2 - x1) * coefficient;
        double y = y1 + (y2 - y1) * coefficient;
        return new Point2D.Double(x, y);
    }

    private static double fadOf(String tag, double defVal, AssEvent event, Time t){
        Time s = new Time(), e = new Time();
        Pattern p = Pattern.compile("fad\\((?<s>\\d+),(?<e>\\d+)\\)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            s = Time.from(Integer.parseInt(m.group("s")), Unit.Millisecond);
            e = Time.from(Integer.parseInt(m.group("e")), Unit.Millisecond);
        }
        Time sThreshold = Time.add(event.getStart(), s);
        Time eThreshold = Time.subtract(event.getEnd(), e);
        if(sThreshold.getMicros() != 0L && Time.isInRange(event.getStart(), sThreshold, t)){
            Time ref = Time.subtract(t, event.getStart());
            return ref.getMicros() / s.getMicros();
        }else if(eThreshold.getMicros() != 0L && Time.isInRange(eThreshold, event.getEnd(), t)){
            Time ref = Time.subtract(t, event.getEnd());
            return ref.getMicros() / e.getMicros();
        }
        return defVal;
    }

    private static double fadeOf(String tag, double defVal, AssEvent event, Time t){
        Time s1 = new Time(), s2 = new Time(), e3 = new Time(), e4 = new Time(), ref1, ref2; // Time
        double a1 = 1d, a2 = 1d, a3 = 1d; // Alpha
        Pattern p = Pattern.compile("fad\\((?<a1>\\d+),(?<a2>\\d+),(?<a3>\\d+)," +
                "(?<s1>\\d+),(?<s2>\\d+),(?<e3>\\d+),(?<e4>\\d+)\\)");
        Matcher m = p.matcher(tag);
        if(m.find()){
            s1 = Time.from(Integer.parseInt(m.group("s1")), Unit.Millisecond);
            s2 = Time.from(Integer.parseInt(m.group("s2")), Unit.Millisecond);
            e3 = Time.from(Integer.parseInt(m.group("e3")), Unit.Millisecond);
            e4 = Time.from(Integer.parseInt(m.group("e4")), Unit.Millisecond);
            a1 = (double) (255 - Integer.parseInt(m.group("a1"))) / 255d;
            a2 = (double) (255 - Integer.parseInt(m.group("a2"))) / 255d;
            a3 = (double) (255 - Integer.parseInt(m.group("a3"))) / 255d;
        }
        // If in before
        ref1 = event.getStart();
        ref2 = Time.add(event.getStart(), s1);
        if(Time.isInRange(ref1 /* start */, ref2 /* t1 */, t)){
            return a1;
        }

        // If after before and before t2
        ref1 = Time.add(event.getStart(), s1);
        ref2 = Time.add(event.getStart(), s2);
        if(Time.isInRange(ref1 /* t1 */, ref2 /* t2 */, t)){
            try{
                Time ref = Time.subtract(t, event.getStart());
                Time diff = Time.subtract(ref2, ref1);
                return a1 + (a2 - a1) * (ref.getMicros() / diff.getMicros());
            }catch(Exception _){
                return a1;
            }
        }

        // If between t2 and t3
        ref1 = Time.add(event.getStart(), s2);
        ref2 = Time.add(event.getStart(), e3);
        if(Time.isInRange(ref1 /* t2 */, ref2 /* t3 */, t)){
            return a2;
        }

        // If before after and after t3
        ref1 = Time.add(event.getStart(), e3);
        ref2 = Time.add(event.getStart(), e4);
        if(Time.isInRange(ref1 /* t3 */, ref2 /* t4 */, t)){
            try{
                Time ref = Time.subtract(t, event.getStart());
                Time diff = Time.subtract(ref2, ref1);
                return a2 + (a3 - a2) * (ref.getMicros() / diff.getMicros());
            }catch(Exception _){
                return a2;
            }
        }

        // If in after
        ref1 = Time.add(event.getStart(), e4);
        ref2 = event.getEnd();
        if(Time.isInRange(ref1 /* t4 */, ref2 /* end */, t)){
            return a3;
        }

        return defVal;
    }

    private static Shape clipOf(String tag, int width, int height){
        if(tag.split(",").length > 2){
            // Rectangle clip
            Pattern p = Pattern.compile("clip\\((?<x1>\\d+),(?<y1>\\d+),(?<x2>\\d+),(?<y2>\\d+)\\)");
            Matcher m = p.matcher(tag);
            if(m.find()){
                int x1 = Integer.parseInt(m.group("x1"));
                int y1 = Integer.parseInt(m.group("y1"));
                int x2 = Integer.parseInt(m.group("x2"));
                int y2 = Integer.parseInt(m.group("y2"));
                return new Rectangle(x1, y1, x2, y2);
            }
        }else{
            // Drawing clip
            Pattern p = Pattern.compile("clip\\((?<scale>\\d+),(?<commands>.*)\\)");
            Matcher m = p.matcher(tag);
            if(m.find()){
                int scale = Integer.parseInt(m.group("scale"));
                String com = m.group("commands");
                return draw(scale, com);
            }
        }
        return new Rectangle(0, 0, width, height);
    }

    private static Shape invisibleClipOf(String tag, int width, int height){
        if(tag.split(",").length > 2){
            // Rectangle clip
            Pattern p = Pattern.compile("iclip\\((?<x1>\\d+),(?<y1>\\d+),(?<x2>\\d+),(?<y2>\\d+)\\)");
            Matcher m = p.matcher(tag);
            if(m.find()){
                int x1 = Integer.parseInt(m.group("x1"));
                int y1 = Integer.parseInt(m.group("y1"));
                int x2 = Integer.parseInt(m.group("x2"));
                int y2 = Integer.parseInt(m.group("y2"));
                return new Rectangle(x1, y1, x2, y2);
            }
        }else{
            // Drawing clip
            Pattern p = Pattern.compile("iclip\\((?<scale>\\d+),(?<commands>.*)\\)");
            Matcher m = p.matcher(tag);
            if(m.find()){
                int scale = Integer.parseInt(m.group("scale"));
                String com = m.group("commands");
                Area wholeImage = new Area(new Rectangle(0, 0, width, height));
                Area drawing = new Area(draw(scale, com));
                wholeImage.subtract(drawing);
                return wholeImage;
            }
        }
        return new Rectangle(0, 0, width, height);
    }

    private static Shape draw(int divide, String commands){
        AffineTransform transform = new AffineTransform();
        if(divide <= 0) divide = 1;
        String copy = commands.trim();
        String lastUsed = "";
        List<Point2D> spline = new ArrayList<>();
        Point2D lastPoint = new Point2D.Double();
        GeneralPath gp = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        while(!copy.isEmpty()){
            if(copy.startsWith("m")){
                Pattern p = Pattern.compile("m\\s(?<x>\\d+.*\\d*)\\s(?<y>\\d+.*\\d*)");
                Matcher m = p.matcher(copy);
                if(m.find()){
                    double x = Double.parseDouble(m.group("x"));
                    double y = Double.parseDouble(m.group("y"));
                    gp.moveTo(x, y);
                    copy = copy.substring(m.end()).trim();
                    lastUsed = "m";
                    lastPoint = new Point2D.Double(x, y);
                }
            }else if(copy.startsWith("n")){
                // Java don't have a function to work with this n tag otherwise lets use m
                Pattern p = Pattern.compile("n\\s(?<x>\\d+.*\\d*)\\s(?<y>\\d+.*\\d*)");
                Matcher m = p.matcher(copy);
                if(m.find()){
                    double x = Double.parseDouble(m.group("x"));
                    double y = Double.parseDouble(m.group("y"));
                    gp.moveTo(x, y);
                    copy = copy.substring(m.end()).trim();
                    lastUsed = "n";
                    lastPoint = new Point2D.Double(x, y);
                }
            }else if(copy.startsWith("l")){
                Pattern p = Pattern.compile("l\\s(?<x>\\d+.*\\d*)\\s(?<y>\\d+.*\\d*)");
                Matcher m = p.matcher(copy);
                if(m.find()){
                    double x = Double.parseDouble(m.group("x"));
                    double y = Double.parseDouble(m.group("y"));
                    gp.lineTo(x, y);
                    copy = copy.substring(m.end()).trim();
                    lastUsed = "l";
                    lastPoint = new Point2D.Double(x, y);
                }
            }else if(copy.startsWith("b")){
                Pattern p = Pattern.compile("b\\s(?<cpx1>\\d+.*\\d*)\\s(?<cpy1>\\d+.*\\d*)" +
                        "\\s(?<cpx2>\\d+.*\\d*)\\s(?<cpy2>\\d+.*\\d*)" +
                        "\\s(?<x3>\\d+.*\\d*)\\s(?<y3>\\d+.*\\d*)");
                Matcher m = p.matcher(copy);
                if(m.find()){
                    double cpx1 = Double.parseDouble(m.group("cpx1"));
                    double cpy1 = Double.parseDouble(m.group("cpy1"));
                    double cpx2 = Double.parseDouble(m.group("cpx2"));
                    double cpy2 = Double.parseDouble(m.group("cpy2"));
                    double x3 = Double.parseDouble(m.group("x3"));
                    double y3 = Double.parseDouble(m.group("y3"));
                    gp.curveTo(cpx1, cpy1, cpx2, cpy2, x3, y3);
                    copy = copy.substring(m.end()).trim();
                    lastUsed = "b";
                    lastPoint = new Point2D.Double(x3, y3);
                }
            }else if(copy.startsWith("s") || copy.startsWith("p") || copy.startsWith("c")){
                lastUsed = copy.substring(0, 1);
                if(lastUsed.equals("s")){
                    spline = new ArrayList<>();
                }else if(lastUsed.equals("c")){
                    // Shape it
                    BSplineCurve bsc = new BSplineCurve(lastPoint);
                    for(Point2D p : spline){
                        bsc.addControlPoint(p);
                    }
                    for(BezierCurve bc : bsc.extractAllBezierCurves()){
                        List<Point2D> p = bc.getControlPoints();
                        gp.curveTo(
                                p.get(1).getX(), p.get(1).getY(),
                                p.get(2).getX(), p.get(2).getY(),
                                p.get(3).getX(), p.get(3).getY()
                        );
                        lastPoint = p.get(3);
                    }
                    continue;
                }
                Pattern p = Pattern.compile("[^mnlbc]\\s(?<x>\\d+.*\\d*)\\s(?<y>\\d+.*\\d*)");
                Matcher m = p.matcher(copy);
                while(m.find()){
                    // Add point
                    spline.add(new Point2D.Double(
                            Double.parseDouble(m.group("x")),
                            Double.parseDouble(m.group("y"))
                    ));
                    copy = copy.substring(m.end()).trim();
                }
            }
        }
        transform.scale((double)1/divide, (double)1/divide);
        return gp.createTransformedShape(transform);
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public Shape getVisibility() {
        return visibility;
    }

    public void setVisibility(Shape visibility) {
        this.visibility = visibility;
    }

    public AssStyle getAssStyle() {
        return assStyle;
    }

    public void setAssStyle(AssStyle assStyle) {
        this.assStyle = assStyle;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
        assStyle.useBold(bold);
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
        assStyle.useItalic(italic);
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
        assStyle.useUnderline(underline);
    }

    public boolean isStrikeOut() {
        return strikeOut;
    }

    public void setStrikeOut(boolean strikeOut) {
        this.strikeOut = strikeOut;
        assStyle.useStrikeOut(strikeOut);
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = colorFromAlphaOf(textColor, textAlpha);
    }

    public Color getKaraokeColor() {
        return karaokeColor;
    }

    public void setKaraokeColor(Color karaokeColor) {
        this.karaokeColor = colorFromAlphaOf(karaokeColor, karaokeAlpha);;
    }

    public Color getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = colorFromAlphaOf(outlineColor, outlineAlpha);;
    }

    public Color getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(Color shadowColor) {
        this.shadowColor = colorFromAlphaOf(shadowColor, shadowAlpha);;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getTextAlpha() {
        return textAlpha;
    }

    public void setTextAlpha(double textAlpha) {
        this.textAlpha = textAlpha;
        setTextColor(colorFromAlphaOf(getTextColor(), textAlpha));
    }

    public double getKaraokeAlpha() {
        return karaokeAlpha;
    }

    public void setKaraokeAlpha(double karaokeAlpha) {
        this.karaokeAlpha = karaokeAlpha;
        setKaraokeColor(colorFromAlphaOf(getKaraokeColor(), karaokeAlpha));
    }

    public double getOutlineAlpha() {
        return outlineAlpha;
    }

    public void setOutlineAlpha(double outlineAlpha) {
        this.outlineAlpha = outlineAlpha;
        setOutlineColor(colorFromAlphaOf(getOutlineColor(), outlineAlpha));
    }

    public double getShadowAlpha() {
        return shadowAlpha;
    }

    public void setShadowAlpha(double shadowAlpha) {
        this.shadowAlpha = shadowAlpha;
        setShadowColor(colorFromAlphaOf(getShadowColor(), shadowAlpha));
    }

    public double getScaleX() {
        return scaleX;
    }

    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
    }

    public double getSpacing() {
        return spacing;
    }

    public void setSpacing(double spacing) {
        this.spacing = spacing;
    }

    public double getAngleX() {
        return angleX;
    }

    public void setAngleX(double angleX) {
        this.angleX = angleX;
    }

    public double getAngleY() {
        return angleY;
    }

    public void setAngleY(double angleY) {
        this.angleY = angleY;
    }

    public double getAngleZ() {
        return angleZ;
    }

    public void setAngleZ(double angleZ) {
        this.angleZ = angleZ;
    }

    public int getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(int borderStyle) {
        this.borderStyle = borderStyle;
    }

    public double getOutlineThicknessX() {
        return outlineThicknessX;
    }

    public void setOutlineThicknessX(double outlineThicknessX) {
        this.outlineThicknessX = outlineThicknessX;
    }

    public double getOutlineThicknessY() {
        return outlineThicknessY;
    }

    public void setOutlineThicknessY(double outlineThicknessY) {
        this.outlineThicknessY = outlineThicknessY;
    }

    public double getShadowShiftX() {
        return shadowShiftX;
    }

    public void setShadowShiftX(double shadowShiftX) {
        this.shadowShiftX = shadowShiftX;
    }

    public double getShadowShiftY() {
        return shadowShiftY;
    }

    public void setShadowShiftY(double shadowShiftY) {
        this.shadowShiftY = shadowShiftY;
    }

    public double getBlurEdge() {
        return blurEdge;
    }

    public void setBlurEdge(double blurEdge) {
        this.blurEdge = blurEdge;
    }

    public double getBlur() {
        return blur;
    }

    public void setBlur(double blur) {
        this.blur = blur;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
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

    public double getMarginT() {
        return marginT;
    }

    public void setMarginT(double marginT) {
        this.marginT = marginT;
    }

    public double getMarginB() {
        return marginB;
    }

    public void setMarginB(double marginB) {
        this.marginB = marginB;
    }

    public int getEncoding() {
        return encoding;
    }

    public void setEncoding(int encoding) {
        this.encoding = encoding;
    }

    public Point2D getOrigin() {
        return origin;
    }

    public void setOrigin(Point2D origin) {
        this.origin = origin;
    }

    public double getShearX() {
        return shearX;
    }

    public void setShearX(double shearX) {
        this.shearX = shearX;
    }

    public double getShearY() {
        return shearY;
    }

    public void setShearY(double shearY) {
        this.shearY = shearY;
    }

    public Time getKaraoke() {
        return karaoke;
    }

    public void setKaraoke(Time karaoke) {
        this.karaoke = karaoke;
    }

    public Time getKaraokeFill() {
        return karaokeFill;
    }

    public void setKaraokeFill(Time karaokeFill) {
        this.karaokeFill = karaokeFill;
    }

    public Time getKaraokeOutline() {
        return karaokeOutline;
    }

    public void setKaraokeOutline(Time karaokeOutline) {
        this.karaokeOutline = karaokeOutline;
    }

    public int getWrapStyle() {
        return wrapStyle;
    }

    public void setWrapStyle(int wrapStyle) {
        this.wrapStyle = wrapStyle;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    @Override
    public Commands clone() {
        try {
            return (Commands) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
