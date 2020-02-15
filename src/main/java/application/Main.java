package application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFrame;

import global.FileHandler;
import global.G;
import global.Settings;

@SuppressWarnings("serial")
public class Main extends JFrame {

    public static void main(String[] args) {
        Settings.load();
        new Main();
    }

    private static final Dimension SCREEN_SIZE = new Dimension(600, 600);
    private static final Dimension SCREEN_OFFSET = new Dimension(16, 53);

    Main() {
        super();
        final Conversion conversion = new Conversion();
        add(conversion);
        add(conversion.getLoadingBar(), BorderLayout.SOUTH);
        setSize(SCREEN_SIZE.width + SCREEN_OFFSET.width, SCREEN_SIZE.height + SCREEN_OFFSET.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        while (true) {
            setTitle(G.FINDING_FILE);
            File file = FileHandler.getFile();
            if (file != null) {
                for (int attempt = 1; attempt <= G.getMaxAttempts(); attempt++) {
                    global.G.reset();
                    setTitle(G.getTitle(attempt));
                    conversion.startConversion(file);
                }
                BufferedImage originalImg = FileHandler.getImage(file);
                file.delete();
                FileHandler.putImageInFile(file, G.ORIGINAL, originalImg, G.BLANK);
            }
        }
    }
}