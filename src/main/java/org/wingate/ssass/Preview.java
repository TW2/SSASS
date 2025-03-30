package org.wingate.ssass;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Preview extends JFrame {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            Preview p = new Preview();
            p.setTitle("Preview");
            p.setLocationRelativeTo(null);
            p.setVisible(true);
        });
    }

    private Preview(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1900, 1000);
        getContentPane().setLayout(new BorderLayout());

        JPanel panTop = new JPanel(new GridLayout(2, 1));
        getContentPane().add(panTop, BorderLayout.NORTH);
        JPanel panCenter = new JPanel(new BorderLayout());
        getContentPane().add(panCenter, BorderLayout.CENTER);

        JPanel panCommands = new JPanel(new BorderLayout());
        JPanel panChoices = new JPanel(new GridLayout(1, 8, 2, 0));
        panTop.add(panCommands);
        panTop.add(panChoices);

        JLabel lblFPS = new JLabel("FPS: ");
        panChoices.add(lblFPS);
        JComboBox<FPS> cbFPS = new JComboBox<>();
        DefaultComboBoxModel<FPS> cbFpsModel = new DefaultComboBoxModel<>();
        cbFPS.setModel(cbFpsModel);
        cbFpsModel.addAll(Arrays.stream(FPS.values()).toList());
        cbFPS.setSelectedIndex(0);
        panChoices.add(cbFPS);
    }

    public enum FPS {
        ProgressiveNTSC("Progressive NTSC", 24000, 1001),
        Film("Film", 24, 1),
        PAL("Interlaced PAL", 25, 1),
        InterlacedNTSC("Interlaced NTSC", 30000, 1001);

        final String name;
        final float fps;

        FPS(String name, int numerator, int denominator){
            this.name = name;
            this.fps = (float) numerator / (float) denominator;
        }

        public String getName() {
            return name;
        }

        public float getFps() {
            return fps;
        }

        @Override
        public String toString() {
            return String.format("%s -> %.03f", getName(), getFps());
        }
    }
}
