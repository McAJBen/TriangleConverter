package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class TrianglesFile {
	
	private static final int FACT = 3;
	private static final double FACT_INVERSE = 1.0 / FACT, 
			MAX_SCORE = Math.pow(195075, 0.5);
	
	private static Random rand = new Random();
	
	private Dimension imageSize;
	private ArrayList<Triangle> triangles = new ArrayList<Triangle>();
	private BufferedImage image;
	
	public TrianglesFile(TrianglesFile tf) {
		this(tf.getTriangles(), tf.imageSize);
	}
	
	public TrianglesFile(ArrayList<Triangle> trArray, Dimension dimension) {
		for (int i = 0; i < trArray.size(); i++) {
			this.triangles.add(trArray.get(i));
		}
		imageSize = (Dimension) dimension.clone();
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
	
	void modifyRandom() {
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandom();
		triangles.remove(i);
		triangles.add(new Triangle());
	}
	
	void modifyShape10() {
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandom();
		double xp[] = triangles.get(i).getXpoints();
		double yp[] = triangles.get(i).getYpoints();
		for (int j = 0; j < 3; j++) {
			xp[j] += rand.nextDouble() / 5 - 0.1;
			yp[j] += rand.nextDouble() / 5 - 0.1;
			
			xp[j] = checkBounds(xp[j], 1);
			yp[j] = checkBounds(yp[j], 1);
		}
		triangles.set(i, new Triangle(xp, yp, triangles.get(i).getColor()));
	}
	
	void modifyShapeFull() {
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandom();
		double xp[] = triangles.get(i).getXpoints();
		double yp[] = triangles.get(i).getYpoints();
		for (int j = 0; j < 3; j++) {
			xp[j] = rand.nextDouble();
			yp[j] = rand.nextDouble();
		}
		triangles.set(i, new Triangle(xp, yp, triangles.get(i).getColor()));
	}
	
	void modifyColor10() {
		if (triangles.size() <= 0) {
			return;
		}
		int i = getRandom();
		int[] col = {triangles.get(i).getRed(),
				triangles.get(i).getGreen(),
				triangles.get(i).getBlue()};
		for (int j = 0; j < col.length; j++) {
			col[j] += rand.nextInt(51) - 25;
			if (col[j] > 255) {
				col[j] = 255;
			}
			else if (col[j] < 0) {
				col[j] = 0;
			}
		}
		triangles.set(i, new Triangle(triangles.get(i).getXpoints(), triangles.get(i).getYpoints(), new Color(col[0], col[1], col[2])));
	}
	
	void modifyRemove() {
		if (triangles.size() > 2) {
			triangles.remove(rand.nextInt(triangles.size()));
		}
	}
	
	private int getRandom() {
		return (int) Math.pow(rand.nextInt((int) Math.pow(triangles.size(), FACT)), FACT_INVERSE);
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
	
	double compare(BufferedImage img) {
		createImg();
		double score = 0;
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				Color a = new Color(img.getRGB(i, j));
				Color b = new Color(image.getRGB(i, j), true);
				if (b.getAlpha() != 255) {
					score += MAX_SCORE;
				}
				else {
					score += Math.sqrt(Math.pow(a.getRed()-b.getRed(), 2)+Math.pow(a.getGreen()-b.getGreen(), 2)+Math.pow(a.getBlue()-b.getBlue(), 2));
				}
			}
		}
		score /= MAX_SCORE;
		score /= (imageSize.getWidth() * imageSize.getHeight());
		return 1-score;
	}
	
	boolean hasAlpha() {
		createImg();
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				if (new Color(image.getRGB(i, j), true).getAlpha() != 255) {
					return true;
				}
			}
		}
		return false;
	}
	
	String getText(double x, double y, double size) {
		String s = "";
		for (int i = 0; i < triangles.size(); i++) {
			s = s.concat(":r" + triangles.get(i).getRed() + "g" + triangles.get(i).getGreen() + "b" + triangles.get(i).getBlue());
			
			for (int j = 0; j < triangles.get(i).getXpoints().length; j++) {
				s = s.concat(
						"x" + (triangles.get(i).getXpoints()[j] * size + x * size) + 
						"y" + (triangles.get(i).getYpoints()[j] * size + y * size));
			}
			s = s.concat("\n");
		}
		return s;
	}
	
	BufferedImage getImage() {
		if (image == null) {
			createImg();
		}
		return image;
	}
	
	int getSize() {
		return triangles.size();
	}
	
	void addTriangle() {
		triangles.add(new Triangle());
	}
	
	void removeBackTriangle() {
		if (getSize() > 0) {
			triangles.remove(0);
		}
	}
	
	ArrayList<Triangle> getTriangles() {
		ArrayList<Triangle> tr = new ArrayList<Triangle>();
		for (int i = 0; i < triangles.size(); i++) {
			tr.add(triangles.get(i).clone());
		}
		return triangles;
	}

	BufferedImage getImage(Dimension newBlockPixelSize) {
		return makeImg(newBlockPixelSize.width, newBlockPixelSize.height);
	}
}
