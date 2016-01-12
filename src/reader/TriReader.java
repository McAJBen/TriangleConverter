package reader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import application.Triangle;

public class TriReader {
	
	public static void main(String args[]) {
		new TriReader();
	}
	
	public TriReader() {
		
		
		int size = 	Integer.parseInt(JOptionPane.showInputDialog("Enter pixel width"));
		
		while (true) {
			File file = null;
			do {
				file = FileHandler.getFile();
				
			} while (file == null);
			
			System.out.println("found file: " + file.getAbsolutePath());
			
			BufferedReader br = null;
			String s = null;
			
			try {
				br = new BufferedReader(new FileReader(file));
				s = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			s = s.substring(s.indexOf(":r") + 2);
			ArrayList<Triangle> triangles = new ArrayList<Triangle>();
			
			int blockSize = 0;
			
			do {
				int r = Integer.parseInt(s.substring(0, s.indexOf("g")));
				s = s.substring(s.indexOf("g") + 1);
				int g = Integer.parseInt(s.substring(0, s.indexOf("b")));
				s = s.substring(s.indexOf("b") + 1);
				int b = Integer.parseInt(s.substring(0, s.indexOf("x")));
				s = s.substring(s.indexOf("x") + 1);
				Color c = new Color(r, g, b);
				ArrayList<Double> xs = new ArrayList<Double>();
				ArrayList<Double> ys = new ArrayList<Double>();
				boolean contin = true;
				while (contin) {
					xs.add(Double.parseDouble(s.substring(0, s.indexOf("y"))));
					s = s.substring(s.indexOf("y") + 1);
					
					if (s.indexOf("x") < s.indexOf(":")) {
						ys.add(Double.parseDouble(s.substring(0, s.indexOf("x"))));
						s = s.substring(s.indexOf("x") + 1);
					}
					else if (s.indexOf(":r") != -1) {
						contin = false;
						ys.add(Double.parseDouble(s.substring(0, s.indexOf(":r"))));
						s = s.substring(s.indexOf(":r") + 2);
					}
					else if (s.indexOf("x") == -1) {
						contin = false;
						ys.add(Double.parseDouble(s));
						s = "";
					}
					else {
						ys.add(Double.parseDouble(s.substring(0, s.indexOf("x"))));
						s = s.substring(s.indexOf("x") + 1);
					}
					
				}
				triangles.add(new Triangle(xs, ys, c));
			} while (s.length() > 0);
			
			System.out.println("sorted: " + file.getAbsolutePath());
			
			blockSize = triangles.size();
			
			blockSize /= 2;
			
			{
				int square = (int) Math.pow(blockSize, 0.5);
				if (Math.pow(square, 2) != blockSize) {
					System.out.println("CAUTION - cannot divide evenly");
				}
				
			}
			
			blockSize = (int) Math.pow(blockSize, 0.5);
			
			Block block = new Block(new Dimension(size, size), triangles);
			
			
			System.out.println(blockSize);
			System.out.println(block.getMaxScore());
			
			while (!block.isDone()) {
				block.move(blockSize);
			}
			
			triangles = block.getTriangleFile().getTriangles();
			
			BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		    Graphics2D g = img.createGraphics();
		    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			for (int i = 0; i < triangles.size(); i++) {
				g.setColor(triangles.get(i).getColor());
				g.fillPolygon(triangles.get(i).getPolygon(size, size));
			}
		    g.dispose();
		    
			
			File fi = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + ".png");
		    try {
		    	ImageIO.write(img, "png", fi);
		    } catch (IOException e) {
		        throw new RuntimeException(e);
		    }
			System.out.println("created picture " + fi.getAbsolutePath());
			
			//FileHandler.saveText(file, block.getTriangleFile().getText(0.0, 0.0, size));
		}
	}
}
