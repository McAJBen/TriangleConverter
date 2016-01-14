package reader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import application.Triangle;

public class TrianglesFile {
	
	private static Random rand = new Random();
	private Dimension imageSize;
	
	private ArrayList<Triangle> triangles = new ArrayList<Triangle>();
	private BufferedImage image;
	
	public TrianglesFile(TrianglesFile tf) {
		for (int i = 0; i < tf.triangles.size(); i++) {
			this.triangles.add(tf.triangles.get(i));
		}
		imageSize = tf.imageSize;
	}
	public TrianglesFile(ArrayList<Triangle> tris, Dimension dimension) {
		triangles = tris;
		imageSize = dimension;
	}
	private void createImg() {
		image = makeImg(imageSize.width, imageSize.height);
	}
	private BufferedImage makeImg(int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = img.createGraphics();
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		for (int i = 0; i < triangles.size(); i++) {
			g.setColor(triangles.get(i).getColor());
			g.fillPolygon(triangles.get(i).getPolygon(width, height));
		}
	    g.dispose();
	    return img;
	}
	public void modifyShape() {
		
		if (triangles.size() <= 0) {
			return;
		}
		int i = rand.nextInt(triangles.size());
		int j = rand.nextInt(3);
		double xp[] = triangles.get(i).getXpoints();
		double yp[] = triangles.get(i).getYpoints();
		
		if (rand.nextBoolean()) {
			xp[j] = change(xp[j]);
		}
		else {
			yp[j] = change(yp[j]);
		}
		triangles.set(i, new Triangle(xp, yp, triangles.get(i).getColor()));
	}
	
	private double change(double val) {
		
		val += rand.nextDouble() / 10 - 0.05;
		
		val = checkBounds(val, 1);
		return val;
	}
	
	private double checkBounds(double n, int max) {
		if (n > max) {
			return max;
		}
		else if (n < 0) {
			return 0;
		}
		return n;
	}
	public boolean hasAlpha() {
		createImg();
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				if (new Color(image.getRGB(i, j), true).getAlpha() == 0) {
					return true;
				}
			}
		}
		return false;
	}
	public String getText(double x, double y, double size) {
		String s = "";
		for (int i = 0; i < triangles.size(); i++) {
			s = s.concat(":r" + triangles.get(i).getRed() + "g" + triangles.get(i).getGreen() + "b" + triangles.get(i).getBlue());
			
			for (int j = 0; j < triangles.get(i).getXpoints().length; j++) {
				s = s.concat(
						"x" + (triangles.get(i).getXpoints()[j] * size + x) + 
						"y" + (triangles.get(i).getYpoints()[j] * size + y));
			}
		}
		return s;
	}
	public double compare() {
		createImg();
		double score = 0;
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				Color b = new Color(image.getRGB(i, j), true);
				if (b.getAlpha() != 255) {
					score++;
				}
			}
		}
		return 1 - (score / (image.getWidth() * image.getHeight()));
	}
	public ArrayList<Triangle> getTriangles() {
		return triangles;
	}
}
