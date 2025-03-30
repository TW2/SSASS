package org.wingate.ssass.sub.ass;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;

public class AssShapeLayout {

    private final TextLayout textLayout;
    private final Shape shape;

    public AssShapeLayout(AssStyle s, String strOrChar) {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setFont(s.getFont());

        textLayout = new TextLayout(strOrChar, s.getAttrs(), g.getFontRenderContext());
        shape = textLayout.getOutline(null);

        g.dispose();
    }

    public TextLayout getTextLayout() {
        return textLayout;
    }

    public Shape getShape() {
        return shape;
    }
}
