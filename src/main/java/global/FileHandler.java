package global;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FileHandler {

    public static File getFile() {
        while (true) {
            try {
                for (File file: (new File(G.USER_DIR)).listFiles()) {
                    if (isValidFile(file)) {
                        return file;
                    }
                }
            } catch (NullPointerException e) { }

            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) { }
        }
    }

    public static BufferedImage getImage(File file) {
        BufferedImage b = null;
        do {
            try {
                b =  ImageIO.read(file);
            } catch (IOException e) {
                System.out.println(G.FILE_ERROR + file.getName());
            }
        } while (b == null);
        return b;
    }

    public static void putImageInFile(File f, String folder, BufferedImage image, String append) {
        File fi = toFile(f, folder, append);
        try {
            if (!fi.exists()) {
                fi.mkdirs();
            }
            ImageIO.write(image, G.PNG, fi);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isValidFile(File f) {
        if (f.isFile()) {

            final String ending = f.getName().substring(f.getName().lastIndexOf('.'));

            switch (ending) {
                case G.DOT_PNG:
                case G.DOT_JPG:
                case G.DOT_BMP:
                    return true;
            }
        }
        return false;
    }

    private static File toFile(File f, String folder, String append) {
        return new File(f.getParent() + File.separator +
                folder + File.separator +
                f.getName().substring(0, f.getName().length() - 4) + append + G.DOT_PNG);
    }
}