package org.wingate.ssass.assassin;

import org.wingate.ssass.assa.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class AssAssIN {
    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("""
//                Command:
//
//                    java -jar AssAssIN.jar -i <?> -o <?>
//
//                Extensions: png, jpg, avif, heic
//
//                Options of input (-i):
//                  "ass <file> <ms> <w>x<h>": Provide an ass file, a time and a size
//                  "js <file> <ms> <w>x<h>": Provide a JavaScript script, time, size
//
//                Options of output (-o):
//                  "f <file>.<extension>": Provide a path or a filename with extension
//                  "p <port> <extension>": Provide a port and extension
//                  "cp <extension>": To pass the data in the clipboard with extension
//                  "debug": To open the result image in an AssAssIN view
//                """);
//        System.out.println("Choose an input and an output:");
//        String choice = scanner.next();
//        System.out.println(choice);


        EventQueue.invokeLater(AssAssIN::new);

    }

    public AssAssIN() {
        JFrame frm = new JFrame("Debug");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel lbl = new JLabel();
        frm.getContentPane().setLayout(new BorderLayout());
        frm.getContentPane().add(lbl, BorderLayout.CENTER);
        BufferedImage img = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, 1280, 720);
        try{
            BufferedImage yakuza = Yakuza.getImage(createAssDemo(), 500d, 1280, 720);
            g.drawImage(yakuza, 0, 0, 1280, 720, null);
        }catch(AssColorException exc){
            System.err.println("Error: malformed color!");
        }
        g.dispose();
        lbl.setIcon(new ImageIcon(img));
        frm.pack();
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
    }

    private ASS createAssDemo(){
        ASS ass = ASS.inMemory();

        AssStyle style = new AssStyle();
        style.setName("AssIN");
        style.setAlignment(new AssAlignment(5));
        style.setAssFont(new AssFont("Arial Unicode MS", 300));

        ass.getStyles().add(style);

        AssEvent event = new AssEvent();
        event.setText("{\\xbord20}Oh !");
        event.setStyle(style);
        event.setType(AssEvent.Type.Dialogue);
        event.setStart(new AssTime(0d));
        event.setEnd(new AssTime(1000.523d));

        ass.getEvents().add(event);

//        event = new AssEvent();
//        event.setText("Le cochon et le lapin rencontrent{\\xbord20} un chat.");
//        event.setStyle(style);
//        event.setType(AssEvent.Type.Dialogue);
//        event.setStart(new AssTime(0d));
//        event.setEnd(new AssTime(1000.523d));
//
//        ass.getEvents().add(event);

//        event = new AssEvent();
//        event.setText("{\\an2\\fscx200\\fscy200}Le cochon et le lapin rencontrent un chat.");
//        event.setStyle(style);
//        event.setType(AssEvent.Type.Dialogue);
//        event.setStart(new AssTime(0d));
//        event.setEnd(new AssTime(1000.523d));
//
//        ass.getEvents().add(event);

//        event = new AssEvent();
//        event.setText("{\\an8\\fscx200\\fscy200}Le cochon et le lapin {\\r}rencontrent {\\fscx200\\fscy200\\1c&HFFFF00&}un chat.");
//        event.setStyle(style);
//        event.setType(AssEvent.Type.Dialogue);
//        event.setStart(new AssTime(0d));
//        event.setEnd(new AssTime(1000.523d));
//
//        ass.getEvents().add(event);

//        event = new AssEvent();
//        event.setText("{\\a10\\pos(100,100)}Le cochon et le {\\1c&H0000FF&}lapin rencontrent un chat.");
//        event.setStyle(style);
//        event.setType(AssEvent.Type.Dialogue);
//        event.setStart(new AssTime(0d));
//        event.setEnd(new AssTime(1000.523d));
//
//        ass.getEvents().add(event);

        return ass;
    }
}
