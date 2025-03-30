package org.wingate.ssass.sub.common;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

public class Style {
    protected final Map<TextAttribute, Object> attrs = new HashMap<>();
    private String fontName;
    private float fontSize;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private boolean strikeOut;
    private Color foregroundColor;

    public Style(String fontName, float fontSize, Color foregroundColor) {
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.foregroundColor = foregroundColor;
        attrs.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
        attrs.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
    }

    public Style() {
        this("Serif", 12f, Color.white);
    }

    //===============================================================================

    public Font getFont(){
        return new Font(attrs);
    }

    //-------------------------------------------------------------------------------

    public void resetToPlain(){
        useBold(false);
        useItalic(false);
        useUnderline(false);
        useStrikeOut(false);
    }

    public void useBold(boolean value){
        attrs.put(TextAttribute.WEIGHT,
                value ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
        bold = value;
    }

    public void useItalic(boolean value){
        attrs.put(TextAttribute.POSTURE,
                value ? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);
        italic = value;
    }

    public void useUnderline(boolean value){
        attrs.put(TextAttribute.UNDERLINE,
                value ? TextAttribute.UNDERLINE_ON : -1);
        underline = value;
    }

    public void useStrikeOut(boolean value){
        attrs.put(TextAttribute.STRIKETHROUGH,
                value ? TextAttribute.STRIKETHROUGH_ON : false);
        strikeOut = value;
    }

    public void useFontName(String fontName){
        this.fontName = fontName;
        attrs.put(TextAttribute.FAMILY, fontName);
    }

    public void useFontSize(float fontSize){
        this.fontSize = fontSize;
        attrs.put(TextAttribute.SIZE, fontSize);
    }

    public void setForegroundColor(Color foregroundColor){
        this.foregroundColor = foregroundColor;
        attrs.put(TextAttribute.FOREGROUND, foregroundColor);
    }

    //===============================================================================

    public String getFontName() {
        return fontName;
    }

    public float getFontSize() {
        return fontSize;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public boolean isUnderline() {
        return underline;
    }

    public boolean isStrikeOut() {
        return strikeOut;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public Map<TextAttribute, Object> getAttrs() {
        return attrs;
    }
}
