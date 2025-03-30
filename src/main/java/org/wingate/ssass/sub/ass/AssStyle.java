package org.wingate.ssass.sub.ass;

import org.wingate.ssass.sub.common.Style;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AssStyle extends Style implements Cloneable {

    private String name;
    private Color textColor;
    private Color karaokeColor;
    private Color outlineColor;
    private Color shadowColor;
    private double scaleX;
    private double scaleY;
    private double spacing;
    private double angle;
    private int borderStyle;
    private double outlineThickness;
    private double shadowShift;
    private int alignment;
    private double marginL;
    private double marginR;
    private double marginV;
    private int encoding;

    public AssStyle(String fontName, float fontSize, Color foregroundColor) {
        super(fontName, fontSize, foregroundColor);
        textColor = foregroundColor;
    }

    public AssStyle() {
        super("Arial", 16f, Color.yellow);
        textColor = Color.yellow;
    }

    //===============================================================================

    public static List<AssShapeLayout> getShapes(AssStyle s, String sentence){
        final List<AssShapeLayout> loc = new ArrayList<>();

        AffineTransform transform = new AffineTransform();

        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();



        g.setColor(Color.yellow);
        s.useFontName("Arial");
        s.useFontSize(52f);

        // Set translate
        transform.translate(10, 40);

        for(char c : sentence.toCharArray()){
            loc.add(new AssShapeLayout(s, Character.toString(c)));
        }

        for(int i=0; i<loc.size(); i++){
            AssShapeLayout l = loc.get(i);

            transform.translate(
                    i == 0 ? 0d : loc.get(i-1).getTextLayout().getAdvance(),
                    0d
            );

            g.setTransform(transform);

            g.fill(l.getShape());
        }

        //-Apply spacing------------------------------------

        //-Apply spacing-(END)------------------------------

        g.dispose();

        // Image
        try{
            ImageIO.write(img, "png", new File("test.png"));
        }catch(Exception _){

        }

        return loc;
    }

    //===============================================================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        setForegroundColor(textColor);
    }

    public Color getKaraokeColor() {
        return karaokeColor;
    }

    public void setKaraokeColor(Color karaokeColor) {
        this.karaokeColor = karaokeColor;
    }

    public Color getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
    }

    public Color getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
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

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public int getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(int borderStyle) {
        this.borderStyle = borderStyle;
    }

    public double getOutlineThickness() {
        return outlineThickness;
    }

    public void setOutlineThickness(double outlineThickness) {
        this.outlineThickness = outlineThickness;
    }

    public double getShadowShift() {
        return shadowShift;
    }

    public void setShadowShift(double shadowShift) {
        this.shadowShift = shadowShift;
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

    public int getEncoding() {
        return encoding;
    }

    public void setEncoding(int encoding) {
        this.encoding = encoding;
    }

    @Override
    public AssStyle clone() {
        try {
            return (AssStyle) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
