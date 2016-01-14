package reader;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import application.Triangle;

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
		return equalsFileExtension(f.getName(), ".trifi");
	}
	
	private static boolean equalsFileExtension(String fileName, String extension) {
		int lastIndex = fileName.lastIndexOf('.');
		if (lastIndex == -1) {
			return false;
		}
		return extension.equals(fileName.substring(lastIndex));
	}
	
	public static void save(File f, BufferedImage newImg) {
		
		File fi = new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + ".png");
	    try {
	    	ImageIO.write(newImg, "png", fi);
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public static void saveText(File f, String header, ArrayList<Triangle> triangles) {
		File fi = new File(f.getParent() + "\\TriFi");
		if (!fi.exists()) {
			fi.mkdirs();
		}
		fi = new File(f.getParent() + "\\TriFi\\" + f.getName().substring(0, f.getName().length() - 6) + ".trifi");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fi));
			writer.write(header + "\n");
			for (int i = 0; i < triangles.size(); i++) {
				String s = ":r" + triangles.get(i).getRed() + "g" + triangles.get(i).getGreen() + "b" + triangles.get(i).getBlue();
				
				for (int j = 0; j < triangles.get(i).getXpoints().length; j++) {
					s = s.concat(
							"x" + (triangles.get(i).getXpoints()[j]) + 
							"y" + (triangles.get(i).getYpoints()[j]));
				}
				s = s.concat("\n");
				writer.write(s);
			}
			writer.close();
		} catch (IOException e) {
			
		}
	}

}
