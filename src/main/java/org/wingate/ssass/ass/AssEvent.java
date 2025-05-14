/*
 * Copyright (C) 2024 util2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wingate.ssass.ass;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author util2
 */
public class AssEvent implements Cloneable {

    public enum Type{
        Comment("Comment"),
        Dialogue("Dialogue"),
        Tagged("Tagged");
        
        final String name;
        
        Type(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }
        
    }

    private Type type;
    private int layer;
    private AssTime start;
    private AssTime end;
    private AssStyle style;
    private AssActor name;
    private int marginL;
    private int marginR;
    private int marginV;
    private AssEffect effect;
    private String text;

    // TODO: Group of ISO-3166 (do a XML-based file to save all data [*.asx for ass xml])

    public AssEvent(){
        type = Type.Dialogue;
        layer = 0;
        start = new AssTime();
        end = new AssTime();
        style = new AssStyle();
        name = new AssActor();
        marginL = 0;
        marginR = 0;
        marginV = 0;
        effect = new AssEffect();
        text = "";
    }

    public static AssEvent createFromRawLine(String rawline, List<AssStyle> ls, List<AssActor> la, List<AssEffect> lf) {
        AssEvent event = new AssEvent();

        String[] t = rawline.split(",", 10);

        event.type    = t[0].startsWith("Dialogue: ") ? Type.Dialogue : Type.Comment;
        event.layer   = Integer.parseInt(t[0].substring(t[0].indexOf(" ") + 1)); // layer
        event.start   = AssTime.create(t[1]); // start
        event.end     = AssTime.create(t[2]); // end
        event.style   = getStyleFromName(ls, t[3]); // style
        event.name    = getActorFromName(la, t[4]); // name
        event.marginL = Integer.parseInt(t[5]); // marginL
        event.marginR = Integer.parseInt(t[6]); // marginR
        event.marginV = Integer.parseInt(t[7]); // marginV
        event.effect  = getEffectFromName(lf, t[8]); // effect
        event.text    = t[9]; // text

        return event;
    }

    public static AssEvent createFromRawLine(String rawline, ASS ass) {
        return createFromRawLine(rawline, ass.getStyles(), ass.getActors(), ass.getEffects());
    }
    
    public String toRawLine(){
        return String.format("%s: %d,%s,%s,%s,%s,%d,%d,%d,%s,%s",
                getType().getName(),        // "%s": %d, %s, %s, %s, %s, %d, %d, %d, %s, %s
                getLayer(),                 // %s: "%d", %s, %s, %s, %s, %d, %d, %d, %s, %s
                getStart().toAss(),         // %s: %d, "%s", %s, %s, %s, %d, %d, %d, %s, %s
                getEnd().toAss(),           // %s: %d, %s, "%s", %s, %s, %d, %d, %d, %s, %s
                getStyle().getName(),       // %s: %d, %s, %s, "%s", %s, %d, %d, %d, %s, %s
                getName().getName(),        // %s: %d, %s, %s, %s, "%s", %d, %d, %d, %s, %s
                getMarginL(),               // %s: %d, %s, %s, %s, %s, "%d", %d, %d, %s, %s
                getMarginR(),               // %s: %d, %s, %s, %s, %s, %d, "%d", %d, %s, %s
                getMarginV(),               // %s: %d, %s, %s, %s, %s, %d, %d, "%d", %s, %s
                getEffect().getName(),      // %s: %d, %s, %s, %s, %s, %d, %d, %d, "%s", %s
                getText()                   // %s: %d, %s, %s, %s, %s, %d, %d, %d, %s, "%s"
        );
    }
    
    private static AssStyle getStyleFromName(List<AssStyle> l, String name){
        AssStyle v = new AssStyle();
        
        for(AssStyle s : l){
            if(s.getName().equals(name)){
                v = s;
                break;
            }
        }
        
        return v;
    }
    
    private static AssActor getActorFromName(List<AssActor> l, String name){
        AssActor v = new AssActor();
        
        for(AssActor s : l){
            if(s.getName().equals(name)){
                v = s;
                break;
            }
        }
        
        return v;
    }
    
    private static AssEffect getEffectFromName(List<AssEffect> l, String name){
        AssEffect v = new AssEffect();
        
        for(AssEffect s : l){
            if(s.getName().equals(name)){
                v = s;
                break;
            }
        }
        
        return v;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public AssTime getStart() {
        return start;
    }

    public void setStart(AssTime start) {
        this.start = start;
    }

    public AssTime getEnd() {
        return end;
    }

    public void setEnd(AssTime end) {
        this.end = end;
    }

    public AssStyle getStyle() {
        return style;
    }

    public void setStyle(AssStyle style) {
        this.style = style;
    }

    public AssActor getName() {
        return name;
    }

    public void setName(AssActor name) {
        this.name = name;
    }

    public int getMarginL() {
        return marginL;
    }

    public void setMarginL(int marginL) {
        this.marginL = marginL;
    }

    public int getMarginR() {
        return marginR;
    }

    public void setMarginR(int marginR) {
        this.marginR = marginR;
    }

    public int getMarginV() {
        return marginV;
    }

    public void setMarginV(int marginV) {
        this.marginV = marginV;
    }

    public AssEffect getEffect() {
        return effect;
    }

    public void setEffect(AssEffect effect) {
        this.effect = effect;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getCPS(){
        // Character per second
        String s = getText();
        
        Pattern p = Pattern.compile("\\{[^\\}]*(?<text>[^\\{]*)");
        Matcher m = p.matcher(s);
        
        StringBuilder sb = new StringBuilder();
        
        while(m.find()){
            sb.append(m.group("text"));
        }
        
        if(!sb.isEmpty()) s = sb.toString();
        
        s = s.replace("\\N", "");
        s = s.replace(" ", "");
        s = s.replace(".", "");
        s = s.replace("?", "");
        s = s.replace("!", "");        
        s = s.replace(",", "");
        s = s.replace(";", "");
        
        return s.length() / 60f;
    }
    
    public float getCPL(){
        // Character per line minus space
        // Taking the greatest piece of line (if two lines separated by \N)
        String s = getText();
        
        Pattern p = Pattern.compile("\\{[^\\}]*(?<text>[^\\{]*)");
        Matcher m = p.matcher(s);
        
        StringBuilder sb = new StringBuilder();
        
        while(m.find()){
            sb.append(m.group("text"));
        }
        
        if(!sb.isEmpty()) s = sb.toString();
        
        s = s.replace(" ", "");
        if(s.contains("\\N")){
            String[] t = s.split("\\\\N");
            String sText = t[0];
            for(String str : t){
                if(str.length() > sText.length()) sText = str;
            }
            s = sText;
        }
        
        return (float)s.length();
    }

    @Override
    public AssEvent clone() {
        try {
            AssEvent clone = (AssEvent) super.clone();
            clone.setStart(clone.getStart().clone());
            clone.setEnd(clone.getEnd().clone());
            clone.setStyle(clone.getStyle().clone());
            clone.setName(clone.getName().clone());
            clone.setEffect(clone.getEffect().clone());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
