package org.wingate.ssass.test;

import org.wingate.ssass.ass.*;
import org.wingate.ssass.output.OutputImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Preview {

    public static void main(String[] args) {
        EventQueue.invokeLater(()->{
            try {
                Scanner scanner = new Scanner(System.in);

                System.out.println("Hello, put here a sentence with or without tags:");
                String text = scanner.nextLine();

                AssStyle style = new AssStyle();
                System.out.println("A style is provided to you:");
                System.out.println(style.toRawLine());

                System.out.println("Change font? (Type 'y' for 'Yes' or 'n' for 'No')");
                String q1 = scanner.nextLine();
                if(q1.equalsIgnoreCase("y")){
                    System.out.println("Your font:");
                    String r1 = scanner.nextLine();
                    style.getAssFont().setName(r1);
                }

                System.out.println("Change size? (Type 'y' for 'Yes' or 'n' for 'No')");
                String q2 = scanner.nextLine();
                if(q2.equalsIgnoreCase("y")){
                    System.out.println("Your size in points:");
                    String r2 = scanner.nextLine();
                    style.getAssFont().setSize(Float.parseFloat(r2));
                }

                System.out.println("Change text color? (Type 'y' for 'Yes' or 'n' for 'No')");
                String q3 = scanner.nextLine();
                if(q3.equalsIgnoreCase("y")){
                    System.out.println("Your color AABBGGRR:");
                    String r3 = scanner.nextLine();
                    style.setTextColor(AssColor.fromScheme(r3, AssColor.Scheme.ABGR));
                }

                System.out.println("Change outline color? (Type 'y' for 'Yes' or 'n' for 'No')");
                String q4 = scanner.nextLine();
                if(q4.equalsIgnoreCase("y")){
                    System.out.println("Your color AABBGGRR:");
                    String r4 = scanner.nextLine();
                    style.setOutlineColor(AssColor.fromScheme(r4, AssColor.Scheme.ABGR));
                }

                AssEvent event = new AssEvent();
                event.setStyle(style);
                event.setText(text);

                ASS ass = new ASS();
                ass.getStyles().add(style);
                ass.getEvents().add(event);

                System.out.println("Type a complete file location to store your png image:");
                String completePath = scanner.nextLine();

                try{
                    ImageIO.write(
                            OutputImage.getImages(ass, event, 1280, 720),
                            "png",
                            new File(completePath)
                    );
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }

                System.out.println("Written!");
            } catch (AssColorException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
