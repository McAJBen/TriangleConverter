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
		int width = 	Integer.parseInt(JOptionPane.showInputDialog("Enter picture width"));
		int height = 	Integer.parseInt(JOptionPane.showInputDialog("Enter picture height"));
		
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
			
			s = s.substring(s.indexOf("b") + 1);
			
			int blockSize = Integer.parseInt(s.substring(0, s.indexOf("t")));
			System.out.println("blksize " + blockSize);
			s = s.substring(s.indexOf("t") + 1);
			
			int trianglesPerBlock = Integer.parseInt(s.substring(0, s.indexOf("|:r")));
			System.out.println("trianglesPerBlock " + trianglesPerBlock);
			s = s.substring(s.indexOf(":r") + 2);
			ArrayList<Triangle> triangles = new ArrayList<Triangle>();
			
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
			
			
			Dimension pixelSize = new Dimension(width / blockSize, height / blockSize);
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			
			for (int j = 0; j < blockSize; j++) {
				for (int i = 0; i < blockSize; i++) {
					System.out.println("Solving block " + i + ", " + j);
					ArrayList<Triangle> tr = new ArrayList<Triangle>();
					for (int k = 0; k < trianglesPerBlock; k++) {
						
						double[] xs = new double[3];
						double[] ys = new double[3];
						for (int l = 0; l < xs.length; l++) {
							xs[l] = triangles.get(0).getXpoints()[l];
							ys[l] = triangles.get(0).getYpoints()[l];
							
							xs[l] *= blockSize;
							ys[l] *= blockSize;
							
							xs[l] -= i;
							ys[l] -= j;
						}
						tr.add(new Triangle(xs, ys, triangles.get(0).getColor()));
						
						triangles.remove(0);
					}
					Block block = new Block(pixelSize, tr);
					
					while (!block.isDone()) {
						block.move(blockSize);
					}
					
					for (int k = 0; k < tr.size(); k++) {
						g.setColor(tr.get(k).getColor());
						g.fillPolygon(tr.get(k).getPolygon(pixelSize.width, pixelSize.height, i * pixelSize.width, j * pixelSize.height));
					}
				}
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
