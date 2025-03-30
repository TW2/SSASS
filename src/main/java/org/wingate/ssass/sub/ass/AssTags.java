package org.wingate.ssass.sub.ass;

import java.util.Arrays;
import java.util.Comparator;

public enum AssTags {
    Reset("Reset", "r", 1), // r
    Bold("Bold", "b", 1), // b
    Italic("Italic", "i", 1), // i
    Underline("Underline", "u", 1), // u
    StrikeOut("StrikeOut", "s", 1), // s
    FontName("FontName", "fn", 1), // fn
    FontSize("FontSize", "fs", 1), // fs
    PrimaryColor("PrimaryColor", "c", 1), // c
    TextColor("TextColor", "1c", 1), // 1c
    KaraokeColor("KaraokeColor", "2c", 1), // 2c
    OutlineColor("OutlineColor", "3c", 1), // 3c
    ShadowColor("ShadowColor", "4c", 1), // 4c
    Alpha("Alpha", "alpha", 1), // alpha
    TextAlpha("TextAlpha", "1a", 1), // 1a
    KaraokeAlpha("KaraokeAlpha", "2a", 1), // 2a
    OutlineAlpha("OutlineAlpha", "3a", 1), // 3a
    ShadowAlpha("ShadowAlpha", "4a", 1), // 4a
    Scale("Scale", "fsc", 1), // fsc
    ScaleX("ScaleX", "fscx", 1), // fsc(x)
    ScaleY("ScaleY", "fscy", 1), // fcs(y)
    Spacing("Spacing", "fsp", 1), // fsp
    Rotation("Rotation", "fr", 1), // fr
    RotationX("RotationX", "frx", 1), // fr(x)
    RotationY("RotationY", "fry", 1), // fr(y)
    RotationZ("RotationZ", "frz", 1), // fr(z)
    BorderStyle("BorderStyle", "bs", 1), // bs
    OutlineThickness("OutlineThickness", "bord", 1), // bord
    OutlineThicknessX("OutlineThicknessX", "xbord", 1), // (x)bord
    OutlineThicknessY("OutlineThicknessY", "ybord", 1), // (y)bord
    ShadowShift("ShadowShift", "shad", 1), // shad
    ShadowShiftX("ShadowShiftX", "xshad", 1), // (x)shad
    ShadowShiftY("ShadowShiftY", "yshad", 1), // (y)shad
    BlurEdge("BlurEdge", "be", 1), // be
    Blur("GaussianBlur", "blur", 1), // blur
    AlignmentLegacy("AlignmentLegacy", "a", 1), // a
    AlignmentNumPad("AlignmentNumPad", "an", 1), // an
    MarginL("MarginL", "ml", 1), // ml
    MarginR("MarginR", "mr", 1), // mr
    MarginV("MarginV", "mv", 1), // mv
    MarginT("MarginT", "mt", 1), // mt
    MarginB("MarginB", "mb", 1), // mb
    FontEncoding("FontEncoding", "fe", 1), // fe
    Origin("Origin", "org", 2), // org
    ShearX("ShearX", "fax", 1), // fa(x)
    ShearY("ShearY", "fay", 1), // fa(y)
    Karaoke("Karaoke", "k", 1), // k
    KaraokeFillLegacy("KaraokeFillLegacy", "K", 1), // K
    KaraokeFill("KaraokeFill", "kf", 1), // kf
    KaraokeOutline("KaraokeOutline", "ko", 1), // ko
    WrapStyle("WrapStyle", "q", 1), // q
    Position("Position", "pos", 2), // pos
    Movement("Movement", "move", 6), // move
    FadeSimple("FadeSimple", "fad", 2), // fad
    FadeComplex("FadeComplex", "fade", 7), // fade
    Animation("Animation", "t", 3), // t
    ClipRectangle("ClipRectangle", "clip", 4), // clip
    InvisibleClipRectangle("InvisibleClipRectangle", "iclip", 4), // (i)clip
    ClipDrawing("ClipDrawing", "clip", 2), // clip
    InvisibleClipDrawing("InvisibleClipDrawing", "iclip", 2), // (i)clip
    Drawing("Drawing", "p", 1), // p
    BaselineOffset("BaselineOffset", "pbo", 1); // pbo

    // (drawing command) m
    // (drawing command) l
    // (drawing command) b
    // (drawing command) s
    // (drawing command) p
    // (drawing command) c

    final String name;
    final String tag;
    final int maxParameters;

    AssTags(String name, String tag, int maxParameters){
        this.name = name;
        this.tag = tag;
        this.maxParameters = maxParameters;
    }

    public static AssTags search(String tag){
        AssTags assTags = null;
        for(AssTags at : Arrays.stream(values()).sorted((o1, o2) -> o1.name.compareTo(o2.name)).toList().reversed()){
            System.out.println(at.tag);
            if(at.tag.startsWith(tag)){
                assTags = at;
                break;
            }
        }
        return assTags;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public int getMaxParameters() {
        return maxParameters;
    }
}
