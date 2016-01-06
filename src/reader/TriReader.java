package reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import application.TrianglesFile;

public class TriReader {
	
	
	public static void main(String[] args) {
		
		File file = null;
		do {
			file = FileHandler.getFile();
		} while (file == null);
		try {
			FileReader br = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// TODO create array list of triangles
		// TODO 
		
		
		
		
		
		
		
		
		
	}
}
