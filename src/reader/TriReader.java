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
import javax.swing.JOptionPane;

import application.Triangle;

public class TriReader {
	
	public static void main(String args[]) {
		new TriReader();
	}
	
	@SuppressWarnings("resource")
	public TriReader() {
		int width = 	Integer.parseInt(JOptionPane.showInputDialog("Enter picture width"));
		int height = 	Integer.parseInt(JOptionPane.showInputDialog("Enter picture height"));
		boolean upscaling =  JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, "Do you want to upscale?", "Tri File Reader", JOptionPane.YES_NO_OPTION);
	    
		while (true) {
			File file = null;
			do {
				file = FileHandler.getFile();
			} while (file == null);
			
			System.out.println("found file: " + file.getAbsolutePath());
			
			BufferedReader br = null;
			
			String header = null;
			try {
				br = new BufferedReader(new FileReader(file));
				header = br.readLine();
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			header = header.substring(header.indexOf("b") + 1);
			
			int blockSize = Integer.parseInt(header.substring(0, header.indexOf("t")));
			System.out.println("blksize " + blockSize);
			header = header.substring(header.indexOf("t") + 1);
			
			int trianglesPerBlock = Integer.parseInt(header.substring(0, header.indexOf("|")));
			System.out.println("trianglesPerBlock " + trianglesPerBlock);
			
			ArrayList<Triangle> triangles = new ArrayList<Triangle>();
			
			while (true) {
				String line = null;
				try {
					line = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (line == null) {
					break;
				}
				line = line.substring(2); // removes ":r"
				Color c; // gets the Color from line
				{
					int r = Integer.parseInt(line.substring(0, line.indexOf("g")));
					line = line.substring(line.indexOf("g") + 1);
					int g = Integer.parseInt(line.substring(0, line.indexOf("b")));
					line = line.substring(line.indexOf("b") + 1);
					int b = Integer.parseInt(line.substring(0, line.indexOf("x")));
					line = line.substring(line.indexOf("x") + 1);
					c = new Color(r, g, b);
				}
				ArrayList<Double> xs = new ArrayList<Double>();
				ArrayList<Double> ys = new ArrayList<Double>();
				
				while (line != null) {
					xs.add(Double.parseDouble(line.substring(0, line.indexOf("y"))));
					line = line.substring(line.indexOf("y") + 1);
					
					
					if (line.indexOf("x") == -1) { // no more points after this one
						ys.add(Double.parseDouble(line));
						line = null;
					}
					else {
						ys.add(Double.parseDouble(line.substring(0, line.indexOf("x"))));
						line = line.substring(line.indexOf("x") + 1);
					}
				}
				triangles.add(new Triangle(xs, ys, c));
			}
			System.out.println("sorted: " + file.getAbsolutePath());
			
			Dimension pixelSize = new Dimension(width / blockSize, height / blockSize);

			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			
			if (upscaling) {
				for (int j = 0; j < blockSize; j++) {
					for (int i = 0; i < blockSize; i++) {
						
						ArrayList<Triangle> tr = new ArrayList<Triangle>();
						for (int k = 0; k < trianglesPerBlock; k++) {
							double[] xs = new double[triangles.get(0).getXpoints().length];
							double[] ys = new double[xs.length];
							for (int l = 0; l < xs.length; l++) {
								
								xs[l] = triangles.get(0).getXpoints()[l];
								ys[l] = triangles.get(0).getYpoints()[l];
								
								xs[l] -= (double)i / blockSize;
								ys[l] -= (double)j / blockSize;
						
								xs[l] *= blockSize;
								ys[l] *= blockSize;
							}
							tr.add(new Triangle(xs, ys, triangles.get(0).getColor()));
							
							triangles.remove(0);
						}
						{
							Block block = new Block(pixelSize, tr);
							while (!block.isDone()) {
								block.move();
							}
							g.drawImage(block.getImage(), pixelSize.width * i, pixelSize.height * j, null);
							tr = block.getTriangleFile().getTriangles();
						}
						for (int k = 0; k < trianglesPerBlock; k++) {
							
							double[] xs = new double[tr.get(0).getXpoints().length];
							double[] ys = new double[xs.length];
							for (int l = 0; l < xs.length; l++) {
								
								xs[l] = tr.get(0).getXpoints()[l];
								ys[l] = tr.get(0).getYpoints()[l];
								
								xs[l] += i;
								ys[l] += j;
								
								xs[l] /= blockSize;
								ys[l] /= blockSize;
							}
							triangles.add(new Triangle(xs, ys, tr.get(0).getColor()));
							tr.remove(0);
						}
					}
					System.out.println("Solving blocks " + (j+1) + "/" + blockSize);
				}
			}
			else {
				for (Triangle t: triangles) {
					g.setColor(t.getColor());
					g.fillPolygon(t.getPolygon(width, height));
				}
			}
			
		    g.dispose();
		    
		    
		    FileHandler.save(file, img);
		    
			FileHandler.saveText(file, "b" + blockSize + "t" + trianglesPerBlock + "|", triangles);
			
			System.out.println("Solved " + file.getAbsolutePath());
			
			return;
		}
	}
}
