package application;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class FileHandler {
	
	private static ArrayList<File> files = new ArrayList<File>();
	
	public static File getFile() {
		if (files.size() == 0) {
			files = getFiles();
		}
		if (files.size() > 0) {
			File f = files.get(0);
			files.remove(0);
			return f;
		}
	    return null;
	}
	
	private static ArrayList<File> getFiles() {
		ArrayList<File> fileList = new ArrayList<File>();
		File directory = new File(System.getProperty("user.dir"));
		File[] dirList = directory.listFiles();
		if (dirList != null) {
			for (File file: dirList) {
				if (file.isFile() && isValidFile(file)) {
					fileList.add(file);
	            }
	        }
		}
		return fileList;
	}
	
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
	
	public static void save(File f, BufferedImage oldImg, BufferedImage newImg) {
		putImageInFile(f, "Original", oldImg);
		putImageInFile(f, "New", newImg);
	}
	
	private static void putImageInFile(File f, String folder, BufferedImage b) {
		File fi = new File(f.getParent() + "\\" + folder + "\\" + f.getName());
	    try {
	    	if (!fi.exists()) {
	    		fi.mkdirs();
	    	}
	    	ImageIO.write(b, "png", fi);
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}

	public static void saveText(File f, ArrayList<StringBuffer> strings) {
		
		File fi = new File(f.getParent() + "\\TriFi");
		if (!fi.exists()) {
			fi.mkdirs();
		}
		
		fi = new File(f.getParent() + "\\TriFi\\" + f.getName().substring(0, f.getName().length() - 4) + ".trifi");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fi));
			writer.write("b" + G.blocksWide + "t" + G.maxTriangles + "|" + "\n");
			ArrayList<String> triStrings = StringBuffer.combineStrings(strings, G.blocksWide);
			
			for (String s: triStrings) {
				writer.write(s);
			}
			
			writer.close();
		} catch (IOException e) {
			
		}
	}
}
