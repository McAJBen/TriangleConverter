package global;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FileHandler {
	
	
	private static boolean isValidFile(File f) {
		return 
			equalsFileExtension(f.getName(), ".png") ||
			equalsFileExtension(f.getName(), ".jpg") ||
			equalsFileExtension(f.getName(), ".bmp");
	}
	
	private static boolean equalsFileExtension(String fileName, String extension) {
		int lastIndex = fileName.lastIndexOf('.');
		if (lastIndex == -1) {
			return false;
		}
		return extension.equals(fileName.substring(lastIndex));
	}
	
	public static File getFile() {
		while (true) {
			try {
				for (File file: (new File(System.getProperty("user.dir"))).listFiles()) {
					if (file.isFile() && isValidFile(file)) {
						return file;
		            }
		        }
			} catch (NullPointerException e) { }
			
			try {
				Thread.sleep(10_000);
			} catch (InterruptedException e) { }
		}
	}
	
	public static void putImageInFile(File f, String folder, BufferedImage image, String append) {
		File fi = toFile(f, folder, append);
	    try {
	    	if (!fi.exists()) {
	    		fi.mkdirs();
	    	}
	    	ImageIO.write(image, "png", fi);
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	private static File toFile(File f, String folder, String append) {
		return new File(f.getParent() + "\\" + folder + "\\" + f.getName().substring(0, f.getName().length() - 4) + append + ".png");
	}

	public static BufferedImage getImage(File file) {
		BufferedImage b = null;
		do {
    		try {
    			b =  ImageIO.read(file);
    		} catch (IOException e) {
    			System.out.println("ERROR: Could not read file " + file.getName());
    		}
    	} while (b == null);
		return b;
	}
}
